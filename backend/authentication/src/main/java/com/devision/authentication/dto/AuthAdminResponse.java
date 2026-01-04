package com.devision.authentication.dto;

public record AuthAdminResponse(String token,
                                String userId,
                                String adminId,
                                String email) {
}
