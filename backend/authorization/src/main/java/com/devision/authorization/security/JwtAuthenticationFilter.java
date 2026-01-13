package com.devision.authorization.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    try {
      String auth = request.getHeader("Authorization");
      if (auth == null || !auth.startsWith("Bearer ")) {
        chain.doFilter(request, response);
        return;
      }

      String token = auth.substring(7).trim().replace("\"", "");
      Claims claims = jwtService.parse(token);

      // optional: if your access token has claim "type=access"
      String type = claims.get("type", String.class);
      if (type != null && !"access".equals(type)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      String userId = claims.getSubject();
      String role = claims.get("role", String.class); // e.g. APPLICANT / SUPER_ADMIN / COMPANY

      if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        var authorities = (role == null || role.isBlank())
          ? List.<SimpleGrantedAuthority>of()
          : List.of(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()));

        var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }

      chain.doFilter(request, response);
    } catch (Exception e) {
      SecurityContextHolder.clearContext();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
}
