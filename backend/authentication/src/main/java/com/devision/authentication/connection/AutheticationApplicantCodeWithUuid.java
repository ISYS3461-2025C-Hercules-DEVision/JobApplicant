package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AutheticationApplicantCodeWithUuid extends DtoWithProcessId {

    private String applicantId;
    private String email;

    public AutheticationApplicantCodeWithUuid() {
    }

    public AutheticationApplicantCodeWithUuid(String correlationId, String applicantId, String email) {
        super(correlationId);
        this.applicantId = applicantId;
        this.email = email;
    }

    public String getApplicantId() {
        return applicantId;
    }
    public String getEmail() {
        return email;
    }

}
