package com.devision.applicant.dto;

import org.jetbrains.annotations.Nullable;
import jakarta.validation.constraints.Size;

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
        Boolean activated,

        @Nullable
        Boolean archived
) {}
