package com.devision.applicant.dto;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.model.WorkExperience;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ApplicantCreateRequest(
        @NotBlank
        @Size(max = 200)
        String fullName,
        @NotBlank
        @Email
        String email,

        String country,
        String city,
        String streetAddress,
        String phoneNumber,
        String objectiveSummary,
        String profileImageUrl,
        List<String> skills,
        List<Education> educations,
        List<WorkExperience> experiences,
        List<MediaPortfolio> mediaPortfolios
) {}
