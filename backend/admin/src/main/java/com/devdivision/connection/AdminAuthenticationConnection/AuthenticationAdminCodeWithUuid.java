package com.devdivision.connection.AdminAuthenticationConnection;

public record AuthenticationAdminCodeWithUuid(String correlationId, String adminEmail, String adminRole) {
}
