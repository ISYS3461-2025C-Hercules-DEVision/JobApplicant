package com.devdivision.dto;

import com.devdivision.internal.entity.AdminRole;

public record AdminCreateRequestDTO( String adminEmail, AdminRole adminRole) {
}
