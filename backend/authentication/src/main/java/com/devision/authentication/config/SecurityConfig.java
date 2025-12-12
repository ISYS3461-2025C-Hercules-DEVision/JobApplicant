package com.devision.authentication.config;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.User;
import com.devision.authentication.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;
    private final JwtService jwtService;

    @Value("${spring.app.auth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    public SecurityConfig(UserService userService,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer,
                          JwtService jwtService) {
        this.userService = userService;
        this.kafkaProducer = kafkaProducer;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google")
                        .successHandler((request, response, authentication) -> {
                            handleOAuth2Success(authentication, response);
                        })
                );

        return http.build();
    }

    private void handleOAuth2Success(Authentication authentication,
                                     HttpServletResponse response) throws java.io.IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        // 1. Create or update User & correlationId
        User user = userService.handleGoogleLogin(attributes);

        // 2. Send Kafka event to Applicant service
        AuthToApplicantEvent event = new AuthToApplicantEvent(
                user.getCorrelationId(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );
        kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_TOPIC, event);

        // 3. Generate JWT
        String jwt = jwtService.generateToken(user);

        // 4. Redirect to front-end with token as query param
        String redirectUrl = frontendRedirectUrl +
                "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
