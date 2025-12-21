package com.devision.applicant.dto;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.WorkExperience;
import org.jetbrains.annotations.Nullable;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.WeakHashMap;

public record ApplicantUpdateRequest(

        @Nullable
        @Size(max = 200)
        String fullName,

        @Nullable
        String country,

        @Nullable
        String city,

        @Nullable
        String streetAddress,

        @Nullable
        String phoneNumber,

        @Nullable
        String profileImageUrl,

        @Nullable
        String objectiveSummary,

        @Nullable
        List<Education> educations,

        @Nullable
        List<WorkExperience> experiences,

        @Nullable
        List<String> skills,

        @Nullable
        Boolean activated,

        @Nullable
        Boolean archived
) {}
