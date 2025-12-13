package com.devdivision.gateway.filter;

import com.devdivision.gateway.security.JwtValidator;
import com.devdivision.gateway.security.PublicEndpoints;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;
    private final PublicEndpoints publicEndpoints;

    public JwtAuthenticationFilter(JwtValidator jwtValidator, PublicEndpoints publicEndpoints) {
        this.jwtValidator = jwtValidator;
        this.publicEndpoints = publicEndpoints;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (publicEndpoints.isPublic(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            JWTClaimsSet claims = jwtValidator.validateAndGetClaims(token);

            String subject = claims.getSubject(); // usually userId/applicantId/email
            String roles = claims.getStringClaim("roles"); // optional (nếu bạn set)

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Subject", subject != null ? subject : "")
                    .header("X-Roles", roles != null ? roles : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
