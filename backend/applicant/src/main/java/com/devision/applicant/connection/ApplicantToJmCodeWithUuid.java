package com.devision.applicant.connection;

import com.devision.applicant.dto.common.common.DtoWithProcessId;

public class ApplicantToJmCodeWithUuid extends DtoWithProcessId {
    private String applicantId;

    public ApplicantToJmCodeWithUuid(){}

    public ApplicantToJmCodeWithUuid(String correlationId, String applicantId){
        super(correlationId);
        this.applicantId = applicantId;
    }

    public String getApplicantId(){
        return applicantId;
    }


}
