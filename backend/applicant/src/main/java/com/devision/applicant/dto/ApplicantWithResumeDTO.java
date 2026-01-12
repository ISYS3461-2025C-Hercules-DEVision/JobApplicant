package com.devision.applicant.dto;

import com.devision.applicant.enums.DegreeType;
import com.devision.applicant.model.Education;
import com.devision.applicant.model.WorkExperience;

import java.math.BigDecimal;
import java.util.List;

public record ApplicantWithResumeDTO(
        String applicantId,
        String fullName,
        String email,
        String country,
        String city,
        String streetAddress,
        String phoneNumber,
        Boolean isActivated,
        Boolean isArchived,
        Boolean employmentStatus,
        String resumeId,

        // Resume fields
        String headline,
        String objective,
        List<Education> education,  // Only need this for degree filter
        List<String> certifications,
        List<WorkExperience> experience,
        List<String> skills,           // For skill filter
        BigDecimal minSalary,
        BigDecimal maxSalary
) {

}
