package com.devision.applicant.dto;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.model.WorkExperience;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ResumeDTO(
        String resumeId,
        String applicantId,

        String headline,
        String objective,

        List<Education> education,       // embedded
        List<WorkExperience> experience, // embedded

        List<String> skills,
        List<String> certifications,

        Instant updatedAt,
        BigDecimal minSalary,
        BigDecimal maxSalary) {
}
