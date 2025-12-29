package com.devdivision.dto;

import com.devdivision.internal.entity.AdminRole;

public record AdminDTO(String adminId, String adminEmail, AdminRole adminRole) {
}
