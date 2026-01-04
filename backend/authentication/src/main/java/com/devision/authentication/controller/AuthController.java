package com.devision.authentication.controller;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.*;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PendingApplicantRequests pendingApplicantRequests;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;
    private final PendingApplicantRequests pendingApplicantRequests;

    public AuthController(UserService userService,
                          JwtService jwtService, PendingApplicantRequests pendingApplicantRequests,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.pendingApplicantRequests = pendingApplicantRequests;
        this.kafkaProducer = kafkaProducer;
        this.pendingApplicantRequests = pendingApplicantRequests;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {

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

            // attach by userId, not email
            userService.attachApplicantToUser(user.getId(), reply.getApplicantId());

            User updated = userService.findById(user.getId());
            if (updated == null) {
                throw new IllegalStateException("User not found after attach: userId=" + user.getId());
            }

            log.info("[REGISTER] Applicant attached successfully. userId={}, email={}, applicantId={}",
                    updated.getId(), updated.getEmail(), updated.getApplicantId());

            jwtUserDto jwtUser = new jwtUserDto(
                    updated.getId(),
                    updated.getEmail(),
                    updated.getApplicantId(),
                    updated.getRole()
            );

            String jwt = jwtService.generateToken(jwtUser);

            // return UPDATED data
            return new AuthResponse(
                    jwt,
                    updated.getId(),
                    updated.getApplicantId(),
                    updated.getEmail(),
                    updated.getFullName()
            );

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


    // -------- LOGIN (LOCAL) --------
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userService.loginLocalUser(request);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole()
        );
        // 3. Generate JWT
        String token = jwtService.generateToken(jwtUser);

        return new AuthResponse(
                token,
                user.getId(),
                user.getApplicantId(),
                user.getEmail(),
                user.getFullName()
        );
    }
    @PostMapping("/admin/login")
    public AuthAdminResponse adminLogin(@RequestBody LoginRequest request) {
        User user = userService.loginLocalAdmin(request);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole()
        );
        // 3. Generate JWT
        String token = jwtService.generateToken(jwtUser);

        return new AuthAdminResponse(
                token,
                user.getId(),
                user.getAdminId(),
                user.getEmail()
        );
    }
}
