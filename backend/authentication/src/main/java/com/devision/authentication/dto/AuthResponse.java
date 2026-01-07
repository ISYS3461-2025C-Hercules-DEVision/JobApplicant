package com.devision.authentication.dto;

public record AuthResponse(String accessToken,
                           String refreshToken,
                           String userId,
                           String applicantId,
                           String email,
                           String fullName) {
}
