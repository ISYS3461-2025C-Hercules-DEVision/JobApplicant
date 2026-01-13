package com.devision.authentication.dto;

import com.devision.authentication.user.entity.UserRole;

import java.util.HashMap;
import java.util.Map;

public record jwtUserDto(
        String userId,
                         String email,
                         String applicantId,
        UserRole role,
        Boolean status
                        ) {
    public Map<String, Object> toClaims() {
        Map<String, Object> claims = new HashMap<>();

        if (email != null) claims.put("email", email);
        if (applicantId != null) claims.put("applicantId", applicantId);
        if (role != null) {
            String r = role.name();
            if ("USER".equalsIgnoreCase(r)) r = "APPLICANT";
            if ("ADMIN".equalsIgnoreCase(r)) r = "SUPER_ADMIN";
            claims.put("role", r);
        }
        if (status != null) claims.put("status", status);
        return claims;
    }
}
