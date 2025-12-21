package com.devision.applicant.dto;

public record EducationDTO(String educationId, String applicantId, String degree, String institution,
                           Integer fromYear, Integer toYear, Double GPA) {
}
