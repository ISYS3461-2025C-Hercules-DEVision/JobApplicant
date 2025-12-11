package com.devision.applicant.dto;
//This missing some parameter
// This should include correlationId and something that after register or login, authentication will send to applicant to get the POSTAPI back
public record AutheticationApplicantCodeWithUuid(String correlationId) {
}
