package com.devdivision.mapper;

import com.devdivision.dto.AdminAuthenticationDtos.AdminCreateRequestDTO;
import com.devdivision.dto.AdminAuthenticationDtos.AdminDTO;
import com.devdivision.internal.entity.Admin;

public class AdminMapper {
    private AdminMapper() {}
    public static Admin toEntity( AdminCreateRequestDTO req) {
        Admin admin = new Admin();
        admin.setAdminEmail(req.adminEmail());
        admin.setAdminRole(req.adminRole());
        return admin;
    }
    public static AdminDTO toDTO(Admin admin) {
        return new AdminDTO(admin.getAdminId(), admin.getAdminEmail(), admin.getAdminRole());
    }
}
