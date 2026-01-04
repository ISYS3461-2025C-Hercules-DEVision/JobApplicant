package com.devision.authentication.config;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.jwtUserDto;
import com.devision.authentication.jwt.JwtAuthenticationFilter;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@EnableMethodSecurity
@Slf4j
@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PendingApplicantRequests pendingApplicantRequests;
    @Value("${app.auth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    public SecurityConfig(UserService userService,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer,
                          JwtService jwtService, JwtAuthenticationFilter jwtAuthenticationFilter,
                          PendingApplicantRequests pendingApplicantRequests
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.pendingApplicantRequests = pendingApplicantRequests;
        this.userService = userService;
        this.kafkaProducer = kafkaProducer;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        //  Role-based APIs
                        .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) ->
                                handleOAuth2Success(request, response, authentication)
                        )
                )
                // Add JWT filter BEFORE UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    private void handleOAuth2Success(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {


        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        User user = userService.handleGoogleLogin(attributes);

        // If already linked to applicant -> DON'T call Kafka
        if (user.getApplicantId() != null && !user.getApplicantId().isBlank()) {
            jwtUserDto jwtUser = new jwtUserDto(user.getId(), user.getEmail(), user.getApplicantId(), user.getRole());
            String jwt = jwtService.generateToken(jwtUser);

            String redirectUrl = frontendRedirectUrl +
                    "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
            return;
        }

        //  (first time only)
        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<AutheticationApplicantCodeWithUuid> future =
                pendingApplicantRequests.create(correlationId);

        AuthToApplicantEvent event = new AuthToApplicantEvent(
                correlationId,
                user.getEmail(),
                user.getFullName(),
                null,
                null,
                null,
                null

        );

        try {
            log.info("[OAUTH2] Sending event to applicant-service. correlationId={}, email={}",
                    correlationId, user.getEmail());

            kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC, event);

            log.info("[OAUTH2] Waiting for applicant reply... correlationId={}", correlationId);

            AutheticationApplicantCodeWithUuid reply = future.get(5, TimeUnit.SECONDS); //

            log.info("[OAUTH2] Applicant reply received. correlationId={}, applicantId={}",
                    correlationId, reply.getApplicantId());

            //  attach by userId, not email
            userService.attachApplicantToUser(user.getId(), reply.getApplicantId());


            User updated = userService.findById(user.getId());
            if (updated == null) {
                throw new IllegalStateException(
                        "User not found after attach: userId=" + user.getId()
                );
            }

            log.info("[OAUTH2] Applicant attached successfully. userId={}, email={}, applicantId={}",
                    updated.getId(), updated.getEmail(), updated.getApplicantId());

            jwtUserDto jwtUser = new jwtUserDto(
                    updated.getId(),
                    updated.getEmail(),
                    updated.getApplicantId(),
                    updated.getRole()
            );

            String jwt = jwtService.generateToken(jwtUser);

            String redirectUrl = frontendRedirectUrl +
                    "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);

        } catch (TimeoutException e) {
            log.error("[OAUTH2] TIMEOUT waiting for applicant reply. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);
            response.sendError(504, "Applicant service timeout");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[OAUTH2] INTERRUPTED while waiting for applicant reply. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);
            response.sendError(500, "Interrupted");

        } catch (ExecutionException e) {
            log.error("[OAUTH2] Reply future failed. correlationId={}, email={}, cause={}",
                    correlationId, user.getEmail(),
                    e.getCause() != null ? e.getCause().getMessage() : "null",
                    e);
            response.sendError(500, "Applicant reply failed");

        } catch (Exception e) {
            log.error("[OAUTH2] Unexpected error. correlationId={}, email={}",
                    correlationId, user.getEmail(), e);
            response.sendError(500, "Unexpected error");

        } finally {
            pendingApplicantRequests.remove(correlationId);
            log.info("[OAUTH2] Pending request removed. correlationId={}", correlationId);
        }

    }
}
