package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AutheticationAdminCodeWithUuid extends DtoWithProcessId {
    private String adminId;


    public AutheticationAdminCodeWithUuid() {
    }

    public AutheticationAdminCodeWithUuid(String correlationId, String adminId) {
        super(correlationId);
        this.adminId = adminId;

    }

    public String getAdminId() {
        return adminId;
    }
}
