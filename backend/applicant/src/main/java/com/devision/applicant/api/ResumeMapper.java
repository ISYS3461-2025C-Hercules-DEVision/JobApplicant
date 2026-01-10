package com.devision.applicant.api;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ResumeDTO;
import com.devision.applicant.dto.ResumeUpdateRequest;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Resume;

import java.time.Instant;
import java.time.LocalDateTime;

public class ResumeMapper {
    private ResumeMapper(){}

    public static Resume toEntity(ResumeDTO req) {
        Resume r = new Resume();
        r.setResumeId(req.resumeId());
        r.setApplicantId(req.applicantId());
        r.setHeadline(req.headline());
        r.setObjective(req.objective());
        r.setEducation(req.education());
        r.setExperience(req.experience());
        r.setSkills(req.skills());
        r.setCertifications(req.certifications());
        r.setMediaPortfolios(req.mediaPortfolios());
        r.setMinSalary(req.minSalary());
        r.setMaxSalary(req.maxSalary());

        return r;
    }

    public static ResumeDTO toDto(Resume r) {
        return new ResumeDTO(
                r.getResumeId(),
                r.getApplicantId(),
                r.getHeadline(),
                r.getObjective(),
                r.getEducation(),
                r.getExperience(),
                r.getSkills(),
                r.getCertifications(),
                r.getMediaPortfolios(),
                r.getUpdatedAt(),
                r.getMinSalary(),
                r.getMaxSalary()
        );
    }

    public static void updateEntity(Resume entity, ResumeUpdateRequest req) {
        if (req.headline() != null) entity.setHeadline(req.headline());
        if (req.objective() != null) entity.setObjective(req.objective());
        if (req.education() != null) entity.setEducation(req.education());
        if (req.experience() != null) entity.setExperience(req.experience());
        if (req.skills() != null) entity.setSkills(req.skills());
        if (req.certifications() != null) entity.setCertifications(req.certifications());
        if (req.mediaPortfolios() != null) entity.setMediaPortfolios(req.mediaPortfolios());
        if (req.minSalary() != null) entity.setMinSalary(req.minSalary());
        if (req.maxSalary() != null) entity.setMaxSalary(req.maxSalary());
        entity.setUpdatedAt(Instant.now());
    }
}
