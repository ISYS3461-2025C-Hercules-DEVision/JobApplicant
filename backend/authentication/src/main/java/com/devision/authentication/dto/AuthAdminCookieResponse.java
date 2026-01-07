package com.devision.authentication.dto;

public record AuthAdminCookieResponse(
        String accessToken,
        String userId,
        String adminId,
        String email,
        Boolean status
) {
}
