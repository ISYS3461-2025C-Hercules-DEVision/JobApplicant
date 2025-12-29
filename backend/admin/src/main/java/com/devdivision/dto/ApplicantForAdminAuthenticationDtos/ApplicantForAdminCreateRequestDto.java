package com.devdivision.dto.ApplicantForAdminAuthenticationDtos;

public record ApplicantForAdminCreateRequestDto(String email,
                                                String fullName,
                                                String phoneNumber,
                                                String country) {
}
