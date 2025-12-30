package com.devision.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Option A:
 * - Gateway validates JWT
 * - Gateway forwards identity headers (X-User-Id, X-Role)
 * - This filter just converts those headers into Spring Security Authentication
 */

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String HDR_USER_ID = "X-User-Id";
    public static final String HDR_ROLE = "X-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // If already authenticated, skip
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing == null || !existing.isAuthenticated()) {
            String userId = request.getHeader(HDR_USER_ID);
            String role = request.getHeader(HDR_ROLE);

            if (userId != null && !userId.isBlank() && role != null && !role.isBlank()) {
                // Normalize role -> ROLE_XXX
                String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(normalized));
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
