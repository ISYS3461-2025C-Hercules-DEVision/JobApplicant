package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AuthenticationApplicantForAdminCodeWithUuid extends DtoWithProcessId {
    private String applicantForAdminId;


    public AuthenticationApplicantForAdminCodeWithUuid() {
    }

    public AuthenticationApplicantForAdminCodeWithUuid(String correlationId, String applicantForAdminId) {
        super(correlationId);
        this.applicantForAdminId = applicantForAdminId;

    }

    public String getApplicantForAdminId() {
        return applicantForAdminId;
    }
}
