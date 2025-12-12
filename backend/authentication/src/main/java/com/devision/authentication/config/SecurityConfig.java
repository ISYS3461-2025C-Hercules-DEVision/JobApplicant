package com.devision.authentication.config;

import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.user.User;
import com.devision.authentication.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;


@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;

    public SecurityConfig(UserService userService,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer) {
        this.userService = userService;
        this.kafkaProducer = kafkaProducer;
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
                            handleOAuth2Success(authentication);
                            // Redirect to your front-end after login
                            response.sendRedirect("http://localhost:5173");
                        })
                );

        return http.build();
    }

    private void handleOAuth2Success(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        // 1. create or load User (and set a new correlationId)
        User user = userService.handleGoogleLogin(attributes);

        // 2. build event from auth -> applicant
        AuthToApplicantEvent event = new AuthToApplicantEvent(
                user.getCorrelationId(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );

        // 3. send via Kafka
        kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_TOPIC, event);
    }
}

