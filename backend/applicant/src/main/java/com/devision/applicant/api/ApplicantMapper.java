package com.devision.applicant.api;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.ApplicantForAdmin;
import com.devision.applicant.dto.ApplicantUpdateRequest;

import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Education;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicantMapper {
    private ApplicantMapper() {}

    public static Applicant toEntity(ApplicantCreateRequest req) {
        Applicant a = new Applicant();
        a.setFullName(req.fullName());
        a.setEmail(req.email());
        a.setCountry(req.country());
        a.setCity(req.city());
        a.setStreetAddress(req.streetAddress());
        a.setPhoneNumber(req.phoneNumber());
        a.setProfileImageUrl(req.profileImageUrl());
        a.setIsActivated(true);
        a.setIsArchived(true);
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        a.setEmploymentStatus(false);
        return a;
    }

    public static void updateEntity(Applicant a, ApplicantUpdateRequest req) {
        if (req.email() != null) a.setEmail(req.email());
        if (req.fullName() != null) a.setFullName(req.fullName());
        if (req.country() != null) a.setCountry(req.country());
        if (req.city() != null) a.setCity(req.city());
        if (req.streetAddress() != null) a.setStreetAddress(req.streetAddress());
        if (req.phoneNumber() != null) a.setPhoneNumber(req.phoneNumber());
        if (req.profileImageUrl() != null) a.setProfileImageUrl(req.profileImageUrl());
        if (req.activated() != null) a.setIsActivated(req.activated());
        if (req.archived() != null) a.setIsArchived(req.archived());
        if (req.employmentStatus() != null) a.setEmploymentStatus(req.employmentStatus());
        a.setUpdatedAt(LocalDateTime.now());
    }

    public static ApplicantDTO toDto(Applicant a) {
        return new ApplicantDTO(
                a.getApplicantId(),
                a.getFullName(),
                a.getEmail(),
                a.getCountry(),
                a.getCity(),
                a.getStreetAddress(),
                a.getPhoneNumber(),
                a.getProfileImageUrl(),
                Boolean.TRUE.equals(a.getIsActivated()),
                Boolean.TRUE.equals(a.getIsArchived()),
                a.getEmploymentStatus(),
                a.getCreatedAt()
        );
    }
    public static List<ApplicantForAdmin> toApplicantForAdmin(List<ApplicantDTO> a) {
        return a.stream()
                .map(dto -> new ApplicantForAdmin(
                        dto.applicantId(),
                        dto.email(),
                        dto.fullName(),
                        dto.country(),
                        dto.createdAt(),
                        dto.activated()
                ))
                .toList();
    }
}
