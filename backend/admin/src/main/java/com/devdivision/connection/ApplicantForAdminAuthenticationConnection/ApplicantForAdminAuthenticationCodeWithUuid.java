package com.devdivision.connection.ApplicantForAdminAuthenticationConnection;

import com.devdivision.dto.DtoWithProcessId;

public class ApplicantForAdminAuthenticationCodeWithUuid extends DtoWithProcessId {
    private String ApplicantForAdminId;

    public ApplicantForAdminAuthenticationCodeWithUuid() {
    }

    public ApplicantForAdminAuthenticationCodeWithUuid(String correlationId, String ApplicantForAdminId) {
        super(correlationId);
        this.ApplicantForAdminId = ApplicantForAdminId;
    }

    public String getApplicantId() {
        return ApplicantForAdminId;
    }

    public void setApplicantId(String ApplicantForAdmin) {
        this.ApplicantForAdminId = ApplicantForAdmin;
    }
}
