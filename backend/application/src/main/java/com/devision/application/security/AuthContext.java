package com.devision.application.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class AuthContext {

    private AuthContext() {}

    public static String requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        return auth.getPrincipal().toString();
    }

    public static void requireRole(String role) {
        requireAnyRole(role);
    }

    public static void requireAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("Unauthenticated");

        Set<String> allowed = Set.of(roles);
        boolean ok = auth.getAuthorities().stream().anyMatch(a -> {
            String authority = a.getAuthority();        // e.g. ROLE_ADMIN
            String r = authority.startsWith("ROLE_") ? authority.substring(5) : authority;
            return allowed.contains(r);
        });

        if (!ok) throw new AccessDeniedException("Forbidden");
    }
}
