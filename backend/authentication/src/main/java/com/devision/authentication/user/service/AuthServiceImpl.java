package com.devision.authentication.user.service;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.*;
import com.devision.authentication.jwt.JwtService;

import com.devision.authentication.jwt.tokenStore.CookieService;
import com.devision.authentication.jwt.tokenStore.RefreshToken;
import com.devision.authentication.jwt.tokenStore.RefreshTokenService;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.redis.TokenRevocationService;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.netty.handler.timeout.TimeoutException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    private final PendingApplicantRequests pendingApplicantRequests;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;


    public AuthCookieResponse register(RegisterRequest request, HttpServletResponse response) {

        User user = userService.registerLocalUser(request);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<AutheticationApplicantCodeWithUuid> future =
                pendingApplicantRequests.create(correlationId);

        AuthToApplicantEvent event = new AuthToApplicantEvent(
                correlationId,
                user.getEmail(),
                user.getFullName(),
                request.phoneNumber(),
                request.country(),
                request.city(),
                request.streetAddress()
        );

        try {
            log.info("[REGISTER] Sending event to applicant-service. correlationId={}, email={}",
                    correlationId, user.getEmail());

            kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC, event);

            log.info("[REGISTER] Waiting for applicant reply... correlationId={}", correlationId);

            AutheticationApplicantCodeWithUuid reply = future.get(5, TimeUnit.SECONDS);

            log.info("[REGISTER] Applicant reply received. correlationId={}, applicantId={}",
                    correlationId, reply.getApplicantId());

            //  attach by userId, not email
            userService.attachApplicantToUser(user.getId(), reply.getApplicantId());

            User updated = userService.findById(user.getId());
            if (updated == null) {
                throw new IllegalStateException("User not found after attach: userId=" + user.getId());
            }

            log.info("[REGISTER] Applicant attached successfully. userId={}, email={}, applicantId={}",
                    updated.getId(), updated.getEmail(), updated.getApplicantId());

            //  Issue refresh cookie + access token
            return issueUserTokens(updated, response);

        } catch (TimeoutException e) {
            log.error("[REGISTER] TIMEOUT waiting for applicant reply. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);

            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Applicant service did not respond in time"
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[REGISTER] INTERRUPTED while waiting for applicant reply. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Interrupted while waiting for applicant response"
            );

        } catch (ExecutionException e) {
            log.error("[REGISTER] Reply future failed. correlationId={}, email={}, cause={}",
                    correlationId, user.getEmail(),
                    e.getCause() != null ? e.getCause().getMessage() : "null",
                    e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Applicant service failed"
            );

        } catch (Exception e) {
            log.error("[REGISTER] Unexpected error. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error"
            );

        } finally {
            pendingApplicantRequests.remove(correlationId);
            log.info("[REGISTER] Pending request removed. correlationId={}", correlationId);
        }
    }

    //  LOGIN USER (sets refresh cookie)
    public AuthCookieResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userService.loginLocalUser(request);
        return issueUserTokens(user, response);
    }

    //  LOGIN ADMIN (sets refresh cookie)
    public AuthAdminCookieResponse adminLogin(LoginRequest request, HttpServletResponse response) {
        User user = userService.loginLocalAdmin(request);

        issueRefreshCookie(user.getId(), response);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole(),
                user.getStatus()
        );

        String accessToken = jwtService.generateAccessToken(jwtUser);

        return new AuthAdminCookieResponse(
                accessToken,
                user.getId(),
                user.getAdminId(),
                user.getEmail(),
                user.getStatus()
        );
    }

    //  REFRESH ACCESS TOKEN (rotation refresh cookie)
    public AuthCookieResponse refreshAccessToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token cookie");
        }

        RefreshToken stored = refreshTokenService.validate(refreshToken);

        Claims claims = jwtService.parseClaims(refreshToken);
        String userId = claims.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (Boolean.FALSE.equals(user.getStatus())) {
            refreshTokenService.revokeRefreshToken(stored);
            cookieService.clearRefreshTokenCookie(response);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been banned.");
        }

        //  rotation
        refreshTokenService.revokeRefreshToken(stored);

        String newRefreshToken = jwtService.generateRefreshToken(userId);
        refreshTokenService.save(userId, newRefreshToken);
        cookieService.setRefreshTokenCookie(response, newRefreshToken);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole(),
                user.getStatus()
        );

        String newAccessToken = jwtService.generateAccessToken(jwtUser);

        return new AuthCookieResponse(
                newAccessToken,
                user.getId(),
                user.getApplicantId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus()
        );
    }

    public void logout(String accessToken, String refreshToken, HttpServletResponse response) {

        // ✅ clear refresh cookie
        cookieService.clearRefreshTokenCookie(response);

        // ✅ revoke ACCESS token in Redis (LOCAL only)
        revokeIfLocal(accessToken);

        // ✅ existing refresh token revoke in DB
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        try {
            RefreshToken stored = refreshTokenService.validate(refreshToken);
            refreshTokenService.revokeRefreshToken(stored);

            // ✅ OPTIONAL (extra safety): revoke refresh token JTI too
            revokeIfLocal(refreshToken);

        } catch (Exception ignored) {
        }
    }

    private void revokeIfLocal(String token) {
        if (token == null || token.isBlank()) return;

        try {
            Claims claims = jwtService.parseClaims(token);

            String provider = claims.get("provider", String.class);
            if (provider == null) provider = "LOCAL";

            // ✅ only revoke non-SSO tokens
            if (!"LOCAL".equalsIgnoreCase(provider)) return;

            String jti = claims.getId();
            if (jti == null || jti.isBlank()) return;

            long expMs = claims.getExpiration().getTime();
            long ttlMs = expMs - System.currentTimeMillis();
            if (ttlMs <= 0) return;

            tokenRevocationService.revoke(jti, ttlMs);

        } catch (Exception ignored) {
        }
    }

    // =========================
    // Helpers
    // =========================
    private AuthCookieResponse issueUserTokens(User user, HttpServletResponse response) {

        issueRefreshCookie(user.getId(), response);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole(),
                user.getStatus()
        );

        String accessToken = jwtService.generateAccessToken(jwtUser);

        return new AuthCookieResponse(
                accessToken,
                user.getId(),
                user.getApplicantId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus()
        );
    }

    private void issueRefreshCookie(String userId, HttpServletResponse response) {
        String refreshToken = jwtService.generateRefreshToken(userId);
        refreshTokenService.save(userId, refreshToken);
        cookieService.setRefreshTokenCookie(response, refreshToken);
    }
}
