package com.devision.applicant.dto;
public record ApplicantDTO(
        String applicantId,
        String fullName,
        String email,
        String country,
        String city,
        String streetAddress,
        String phoneNumber,
        String profileImageUrl,
        boolean activated,
        boolean archived
) {}

