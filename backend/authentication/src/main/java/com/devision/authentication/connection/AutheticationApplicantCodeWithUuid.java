package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AutheticationApplicantCodeWithUuid extends DtoWithProcessId {

    private String applicantId;

    public AutheticationApplicantCodeWithUuid() {
    }

    public AutheticationApplicantCodeWithUuid(String correlationId, String applicantId) {
        super(correlationId);
        this.applicantId = applicantId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
}
