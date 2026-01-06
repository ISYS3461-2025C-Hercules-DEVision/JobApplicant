package com.devision.applicant.dto;

import java.util.List;

public record SearchProfileUpdateRequest(String profileName,
                                         String desiredCountry,
                                         String desiredCity,
                                         Double desiredMinSalary,
                                         Double desiredMaxSalary,
                                         List<String> jobTitles,
                                         List<String> technicalBackground,
                                         List<String> employmentStatus,
                                         Boolean isActive) {
}
