package com.devision.applicant.dto;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.model.WorkExperience;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicantDTO(
        String applicantId,
        String fullName,
        String email,
        String country,
        String city,
        String streetAddress,
        String phoneNumber,
        String objectiveSummary,
        String profileImageUrl,
        boolean activated,
        boolean archived,
        LocalDateTime createdAt,
        List<Education> educations,
        List<WorkExperience> experiences,
        List<String> skills,
        List<MediaPortfolio> mediaPortfolios
) {}

