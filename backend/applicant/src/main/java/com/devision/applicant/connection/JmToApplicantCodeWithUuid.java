package com.devision.applicant.connection;

public record JmToApplicantCodeWithUuid(String correlationId, String applicantId){
    public String getCorrelationId(){
        return correlationId;
    }

    public String getApplicantId(){
        return applicantId;
    }


}
