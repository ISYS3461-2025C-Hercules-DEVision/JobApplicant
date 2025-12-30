package com.devision.config.security;

import com.devision.application.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // health / docs (tùy bạn)
                        .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Applicant
                        .requestMatchers("/api/v1/applications/**").hasAnyRole("APPLICANT", "ADMIN")

                        // Partner / JM / Company
                        .requestMatchers("/api/v1/copmpany/applications/**").hasAnyRole("COMPANY", "ADMIN")

                        // Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // default
                        .anyRequest().authenticated()
                )

                // IMPORTANT: register filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
