package com.devision.application.security;

import com.devision.application.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

        // ✅ Admin: list all applications
        .requestMatchers(HttpMethod.GET, "/api/v1/applications").hasRole("SUPER_ADMIN")

        // Applicant apply
        .requestMatchers(HttpMethod.POST, "/api/v1/applications").hasRole("APPLICANT")

        // Applicant view their own applications (we’ll keep authenticated for now; owner-check later)
        .requestMatchers(HttpMethod.GET, "/api/v1/applications/applicant/**").hasRole("APPLICANT")

        // Company view applications by company/job (if you use it)
        .requestMatchers(HttpMethod.GET, "/api/v1/applications/*/job-posts/*/applications").hasRole("COMPANY")

        // Status update: company or admin (tune this based on your business rule)
        .requestMatchers(HttpMethod.PATCH, "/api/v1/applications/*/status").hasAnyRole("SUPER_ADMIN", "COMPANY")

        .anyRequest().authenticated()
      )
      .httpBasic(b -> b.disable())
      .formLogin(f -> f.disable());

    return http.build();
  }
}
