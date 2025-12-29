package com.devision.authentication.controller;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.connection.AuthenticationApplicantForAdminCodeWithUuid;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.*;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantForAdminRequests;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final PendingApplicantForAdminRequests pendingApplicantForAdminRequests;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;

    public AuthController(UserService userService,
                          JwtService jwtService, PendingApplicantRequests pendingApplicantRequests, PendingApplicantForAdminRequests pendingApplicantForAdminRequests,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.pendingApplicantRequests = pendingApplicantRequests;
        this.pendingApplicantForAdminRequests = pendingApplicantForAdminRequests;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {

        log.info("[REGISTER] BEFORE registerLocalUser email={}", request.email());
        User user = userService.registerLocalUser(request);
        log.info("[REGISTER] AFTER registerLocalUser userId={} email={}", user.getId(), user.getEmail());
        String correlationId = UUID.randomUUID().toString();

        log.info("[REGISTER] START correlationId={} email={} fullName={}",
                correlationId, user.getEmail(), user.getFullName());

        CompletableFuture<AutheticationApplicantCodeWithUuid> futureApplicant =
                pendingApplicantRequests.create(correlationId);

        CompletableFuture<AuthenticationApplicantForAdminCodeWithUuid> futureApplicantForAdmin =
                pendingApplicantForAdminRequests.create(correlationId);

        log.info("[REGISTER] Futures created correlationId={} pendingApplicantRequests={} pendingApplicantForAdminRequests={}",
                correlationId, pendingApplicantRequests, pendingApplicantForAdminRequests);

        AuthToApplicantEvent event = new AuthToApplicantEvent(
                correlationId,
                user.getEmail(),
                user.getFullName(),
                request.phoneNumber(),
                request.country(),
                request.city(),
                request.streetAddress()
        );

        long start = System.currentTimeMillis();

        try {
            log.info("[REGISTER] Sending Kafka events correlationId={} -> topics=[{}, {}]",
                    correlationId,
                    KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC,
                    KafkaConstant.AUTHENTICATION_APPLICANT_FOR_ADMIN_TOPIC);

            kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC, event);
            log.info("[REGISTER] Sent to applicant topic OK correlationId={} topic={}",
                    correlationId, KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC);

            kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_FOR_ADMIN_TOPIC, event);
            log.info("[REGISTER] Sent to admin(applicant-for-admin) topic OK correlationId={} topic={}",
                    correlationId, KafkaConstant.AUTHENTICATION_APPLICANT_FOR_ADMIN_TOPIC);

            // ----------------- wait applicant reply -----------------
            log.info("[REGISTER] Waiting Applicant reply correlationId={} timeout={}s",
                    correlationId, 5);

            long t1 = System.currentTimeMillis();
            AutheticationApplicantCodeWithUuid applicantReply =
                    futureApplicant.get(5, TimeUnit.SECONDS);
            long applicantMs = System.currentTimeMillis() - t1;

            log.info("[REGISTER] Applicant reply RECEIVED correlationId={} in {}ms applicantId={}",
                    correlationId, applicantMs, applicantReply.getApplicantId());

            // ----------------- wait admin reply -----------------
            log.info("[REGISTER] Waiting Admin(applicant-for-admin) reply correlationId={} timeout={}s",
                    correlationId, 5);

            long t2 = System.currentTimeMillis();
            AuthenticationApplicantForAdminCodeWithUuid applicantForAdminReply =
                    futureApplicantForAdmin.get(5, TimeUnit.SECONDS);
            long adminMs = System.currentTimeMillis() - t2;

            log.info("[REGISTER] Admin(applicant-for-admin) reply RECEIVED correlationId={} in {}ms applicantForAdminId={}",
                    correlationId, adminMs, applicantForAdminReply.getApplicantForAdminId());

            // ✅ IMPORTANT: Attach IDs to user (you are missing this currently)
            log.info("[REGISTER] Attaching IDs to user correlationId={} userId={} applicantId={} applicantForAdminId={}",
                    correlationId,
                    user.getId(),
                    applicantReply.getApplicantId(),
                    applicantForAdminReply.getApplicantForAdminId());

            userService.attachApplicantToUser(user.getId(), applicantReply.getApplicantId());
            userService.attachApplicantForAdminToUser(user.getId(), applicantForAdminReply.getApplicantForAdminId());

            User updated = userService.findById(user.getId());
            if (updated == null) {
                log.error("[REGISTER] User not found after attach correlationId={} userId={}",
                        correlationId, user.getId());
                throw new IllegalStateException("User not found after attach userId=" + user.getId());
            }

            log.info("[REGISTER] User updated correlationId={} userId={} applicantId={} applicantForAdminId={}",
                    correlationId,
                    updated.getId(),
                    updated.getApplicantId(),
                    updated.getApplicantForAdminId());

            jwtUserDto jwtUser = new jwtUserDto(
                    updated.getId(),
                    updated.getEmail(),
                    updated.getApplicantId(),
                    updated.getRole()
            );

            String token = jwtService.generateToken(jwtUser);

            long totalMs = System.currentTimeMillis() - start;
            log.info("[REGISTER] SUCCESS correlationId={} totalTime={}ms userId={} applicantId={} applicantForAdminId={}",
                    correlationId, totalMs,
                    updated.getId(),
                    updated.getApplicantId(),
                    updated.getApplicantForAdminId());

            return new AuthResponse(
                    token,
                    updated.getId(),
                    updated.getApplicantId(),
                    updated.getEmail(),
                    updated.getFullName()
            );

        } catch (TimeoutException e) {
            long totalMs = System.currentTimeMillis() - start;

            // ✅ super important: tell WHICH future is still not completed
            log.error("[REGISTER] TIMEOUT correlationId={} totalTime={}ms applicantFutureDone={} adminFutureDone={} email={}",
                    correlationId, totalMs,
                    futureApplicant.isDone(),
                    futureApplicantForAdmin.isDone(),
                    user.getEmail(),
                    e);

            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Timeout waiting for applicant/admin reply"
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[REGISTER] INTERRUPTED correlationId={} email={}", correlationId, user.getEmail(), e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Request interrupted"
            );

        } catch (ExecutionException e) {
            log.error("[REGISTER] EXECUTION FAILED correlationId={} email={} cause={}",
                    correlationId, user.getEmail(),
                    e.getCause() != null ? e.getCause().getMessage() : "null",
                    e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Reply future failed: " +
                            (e.getCause() != null ? e.getCause().getMessage() : "unknown")
            );

        } catch (Exception e) {
            log.error("[REGISTER] UNEXPECTED ERROR correlationId={} email={}", correlationId, user.getEmail(), e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error"
            );

        } finally {
            pendingApplicantRequests.remove(correlationId);
            pendingApplicantForAdminRequests.remove(correlationId);
            log.info("[REGISTER] FINALLY removed pending futures correlationId={}", correlationId);
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
