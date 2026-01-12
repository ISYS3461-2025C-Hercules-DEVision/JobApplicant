package com.devision.authentication.jwt;

import com.devision.authentication.redis.TokenRevocationService;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.repo.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final TokenRevocationService tokenRevocationService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim().replace("\"", "");

        try {
            // parse claims (throws if invalid / expired)
            Claims claims = jwtService.parseClaims(token);

            // ensure token is ACCESS token
            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Invalid token type\"}");
                return;
            }

            //  only revoke/check for NON-SSO (LOCAL) accounts
            String provider = claims.get("provider", String.class); // "LOCAL" or "GOOGLE"
            if (provider == null) provider = "LOCAL"; // safe default if you haven't rolled out provider yet

            if ("LOCAL".equalsIgnoreCase(provider)) {
                String jti = claims.getId(); // standard JTI claim
                if (jti == null || jti.isBlank()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Missing token id\"}");
                    return;
                }

                if (tokenRevocationService.isRevoked(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Token is revoked\"}");
                    return;
                }
            }

            String userId = claims.getSubject();

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepo.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//                // banned check -> 403
//                if (Boolean.FALSE.equals(user.getStatus())) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    response.setContentType("application/json");
//                    response.getWriter().write("{\"message\":\"Your account has been banned\"}");
//                    return;
//                }

                // always trust DB role (not token)
                String dbRole = user.getRole().name();

                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + dbRole));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Unauthorized\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
