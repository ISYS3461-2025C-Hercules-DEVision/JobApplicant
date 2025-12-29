package com.devdivision.dto.ApplicantForAdminAuthenticationDtos;

public record ApplicantForAdminDto(
        String id,
        String email,
                                   String fullName,
                                   String phoneNumber,
                                   String country,
        Boolean isActivated) {
}
