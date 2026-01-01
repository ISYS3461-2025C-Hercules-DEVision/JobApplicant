package com.devdivision.connection;

import com.devdivision.internal.entity.AdminRole;

public record AuthenticationAdminCodeWithUuid(String correlationId, String adminEmail, String adminRole) {
}
