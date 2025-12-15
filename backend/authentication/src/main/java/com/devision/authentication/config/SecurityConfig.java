package com.devision.authentication.config;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.dto.UserDto;
import com.devision.authentication.dto.jwtUserDto;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_consumer.PendingApplicantRequests;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.User;
import com.devision.authentication.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;
    private final JwtService jwtService;
    PendingApplicantRequests pendingApplicantRequests;
    @Value("${app.auth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    public SecurityConfig(UserService userService,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer,
                          JwtService jwtService,
                          PendingApplicantRequests pendingApplicantRequests
    ) {
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
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) ->
                                handleOAuth2Success(request, response, authentication)
                        )
                );

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

        // ✅ If already linked to applicant -> DON'T call Kafka, DON'T block
        if (user.getApplicantId() != null && !user.getApplicantId().isBlank()) {
            jwtUserDto jwtUser = new jwtUserDto(user.getId(), user.getEmail(), user.getApplicantId());
            String jwt = jwtService.generateToken(jwtUser);

            String redirectUrl = frontendRedirectUrl +
                    "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
            return;
        }

        // ✅ Only here do we call applicant-service (first time only)
        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<AutheticationApplicantCodeWithUuid> future =
                pendingApplicantRequests.create(correlationId);

        AuthToApplicantEvent event = new AuthToApplicantEvent(
                user.getEmail(),
                user.getFullName(),
                correlationId
        );

        try {
            kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_TOPIC, event);

            AutheticationApplicantCodeWithUuid reply = future.get(5, TimeUnit.SECONDS);

            userService.attachApplicantToUser(user.getEmail(), reply.getApplicantId());
            User updated = userService.findByEmail(user.getEmail());

            jwtUserDto jwtUser = new jwtUserDto(
                    updated.getId(),
                    updated.getEmail(),
                    updated.getApplicantId()
            );

            String jwt = jwtService.generateToken(jwtUser);

            String redirectUrl = frontendRedirectUrl +
                    "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);

        } catch (TimeoutException e) {
            response.sendError(504, "Applicant service timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(500, "Interrupted");
        } catch (ExecutionException e) {
            response.sendError(500, "Applicant reply failed");
        } finally {
            pendingApplicantRequests.remove(correlationId);
        }

    }
}
