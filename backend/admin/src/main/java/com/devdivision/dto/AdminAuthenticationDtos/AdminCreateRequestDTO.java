package com.devdivision.dto.AdminAuthenticationDtos;

import com.devdivision.internal.entity.AdminRole;

public record AdminCreateRequestDTO( String adminEmail, AdminRole adminRole) {
}
