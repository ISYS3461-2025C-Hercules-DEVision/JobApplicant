package com.devdivision.connection;

import com.devdivision.dto.DtoWithProcessId;

public class AdminAuthenticationCodeWithUuid extends DtoWithProcessId {
    private String adminId;

    public AdminAuthenticationCodeWithUuid() {
    }

    public AdminAuthenticationCodeWithUuid(String correlationId, String adminId) {
        super(correlationId);
        this.adminId = adminId;
    }

    public String getApplicantId() {
        return adminId;
    }

    public void setApplicantId(String applicantId) {
        this.adminId = applicantId;
    }
}
