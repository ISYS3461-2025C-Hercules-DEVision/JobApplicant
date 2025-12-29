package com.devision.authentication.user.service;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToAdminEvent;
import com.devision.authentication.connection.AutheticationAdminCodeWithUuid;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.jwtUserDto;
import com.devision.authentication.kafka.kafka_consumer.PendingAdminRequests;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.entity.UserRole;
import com.devision.authentication.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Slf4j
@Component
public class SuperAdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaGenericProducer<AuthToAdminEvent>  kafkaGenericProducer;
    private final UserService userService;
    @Value("${app.super-admin.email}")
    private String superAdminEmail;
    @Value("${app.super-admin.password}")
    private String superAdminPassword;
    private final PendingAdminRequests pendingAdminRequests;
    public SuperAdminSeeder(UserRepository userRepository,
                            PasswordEncoder passwordEncoder, KafkaGenericProducer<AuthToAdminEvent> kafkaGenericProducer, UserService userService,  PendingAdminRequests pendingAdminRequests) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.userService = userService;
        this.pendingAdminRequests = pendingAdminRequests;

    }
    @Override
    public void run(String... args) throws Exception {

        if (superAdminEmail == null || superAdminEmail.isBlank()
                || superAdminPassword == null || superAdminPassword.isBlank()) {
            throw new RuntimeException("SuperAdmin email or password is blank");
        }

        boolean userExists = userRepository.existsByRole(UserRole.SUPER_ADMIN);
        if (userExists) {
            return;
        }

        User superAdmin = User.builder()
                .email(superAdminEmail)
                .fullName("Super Admin")
                .password(passwordEncoder.encode(superAdminPassword))
                .provider("LOCAL")
                .role(UserRole.SUPER_ADMIN)
                .build();

        userRepository.save(superAdmin);
        log.info("Super Admin seeded: {}", superAdminEmail);

        // --- Kafka request/reply ---
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<AutheticationAdminCodeWithUuid> future =
                pendingAdminRequests.create(correlationId);

        AuthToAdminEvent event = new AuthToAdminEvent(correlationId, superAdminEmail);

        try {
            kafkaGenericProducer.sendMessage(KafkaConstant.AUTHENTICATION_ADMIN_TOPIC, event);

            log.info("[SEED] Waiting for admin reply... correlationId={}", correlationId);

            AutheticationAdminCodeWithUuid reply = future.get(5, TimeUnit.SECONDS);

            log.info("[SEED] Admin reply received. correlationId={}, adminId={}",
                    correlationId, reply.getAdminId()); // change getter to correct field

            userService.attachAdminToUser(superAdmin.getId(), reply.getAdminId());

            log.info("[SEED] Admin attached successfully. userId={}, email={}, adminId={}",
                    superAdmin.getId(), superAdmin.getEmail(), reply.getAdminId());

        } catch (TimeoutException e) {
            log.error("[SEED] TIMEOUT waiting for admin reply. correlationId={}, email={}",
                    correlationId, superAdminEmail, e);
            throw e;

        } finally {
            pendingAdminRequests.remove(correlationId);
            log.info("[SEED] Pending request removed. correlationId={}", correlationId);
        }
    }

}
