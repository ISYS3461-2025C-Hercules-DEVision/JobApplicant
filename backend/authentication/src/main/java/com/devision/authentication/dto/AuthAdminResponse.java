package com.devision.authentication.dto;

public record AuthAdminResponse(String accessToken,
                                String refreshToken,
                                String userId,
                                String adminId,
                                String email) {
}
