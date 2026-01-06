package com.devision.applicant.dto;

import java.time.Instant;
import java.util.List;

public record SearchProfileDTO(String searchProfileId,
                               String applicantId,
                               String profileName,
                               String desiredCountry,
                               String desiredCity,
                               Double desiredMinSalary,
                               Double desiredMaxSalary,
                               List<String> jobTitles,
                               List<String> technicalBackground,
                               List<String> employmentStatus,
                               Boolean isActive,
                               Instant createdAt,
                               Instant updatedAt) {
}
