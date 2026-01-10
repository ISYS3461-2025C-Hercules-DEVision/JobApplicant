package com.devision.applicant.dto;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.WorkExperience;
import org.jetbrains.annotations.Nullable;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.WeakHashMap;

public record ApplicantUpdateRequest(

        String email,

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
        Boolean employmentStatus,

        @Nullable
        Boolean activated,

        @Nullable
        Boolean archived
) {}
