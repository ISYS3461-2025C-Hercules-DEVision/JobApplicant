package com.devision.applicant.api;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.ApplicantUpdateRequest;
import com.devision.applicant.dto.EducationDTO;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Education;

import java.time.LocalDateTime;

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
        a.setObjectiveSummary(req.objectiveSummary());
        a.setEducations(req.educations());
        a.setExperiences(req.experiences());
        a.setProfileImageUrl(req.profileImageUrl());
        a.setIsActivated(false);
        a.setIsArchived(false);
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        return a;
    }

    public static void updateEntity(Applicant a, ApplicantUpdateRequest req) {
        if (req.fullName() != null) a.setFullName(req.fullName());
        if (req.country() != null) a.setCountry(req.country());
        if (req.city() != null) a.setCity(req.city());
        if (req.streetAddress() != null) a.setStreetAddress(req.streetAddress());
        if (req.phoneNumber() != null) a.setPhoneNumber(req.phoneNumber());
        if (req.profileImageUrl() != null) a.setProfileImageUrl(req.profileImageUrl());
        if (req.objectiveSummary() != null) a.setObjectiveSummary(req.objectiveSummary());
        if (req.educations() != null) a.setEducations(req.educations());
        if (req.experiences() != null) a.setExperiences(req.experiences());
        if (req.skills() != null) a.setSkills(req.skills());
        if (req.activated() != null) a.setIsActivated(req.activated());
        if (req.archived() != null) a.setIsArchived(req.archived());
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
                a.getObjectiveSummary(),
                a.getEducations(),
                a.getExperiences(),
                a.getSkills(),
                a.getMediaPortfolios(),
                a.getProfileImageUrl(),
                Boolean.TRUE.equals(a.getIsActivated()),
                Boolean.TRUE.equals(a.getIsArchived())
        );
    }
}
