package com.devision.applicant.connection;

public record JmToApplicantCodeWithUuid(String correlationId, Long id, String jmCode) {
    public String getCorrelationId(){ return correlationId;}

    public String getJmCode(){
        return jmCode;
    }

}
