package com.devision.applicant.dto;

public record AutheticationApplicantCodeWithUuid(String correlationId, String id, String fullName, String email ) {
    public String getCorrelationId() {
        return correlationId;
    }
    public String getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getFullName() {
        return fullName;
    }



}
