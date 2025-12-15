package com.devision.config.infra.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicEndpoints {

    // những path KHÔNG cần JWT (whitelist)
    private final List<String> whitelistPrefixes = List.of(
            "/auth/",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/actuator",
            "/discovery/"
    );

    public boolean isPublic(String path) {
        if (path == null) return true;
        return whitelistPrefixes.stream().anyMatch(path::startsWith);
    }
}
