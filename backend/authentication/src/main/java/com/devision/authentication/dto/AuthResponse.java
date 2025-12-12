package com.devision.authentication.dto;

public record AuthResponse(String token,
                           String userId,
                           String applicantId,
                           String email,
                           String fullName) {
}
