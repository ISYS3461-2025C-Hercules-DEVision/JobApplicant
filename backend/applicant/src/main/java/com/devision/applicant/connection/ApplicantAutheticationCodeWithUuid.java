package com.devision.applicant.connection;

import com.devision.applicant.dto.common.common.DtoWithProcessId;

public class ApplicantAutheticationCodeWithUuid extends DtoWithProcessId {

    private String applicantId;

    public ApplicantAutheticationCodeWithUuid() {
    }

    public ApplicantAutheticationCodeWithUuid(String correlationId, String applicantId,  String email) {
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
