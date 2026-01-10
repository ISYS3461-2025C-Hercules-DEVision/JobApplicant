package com.devision.applicant.api;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ResumeDTO;
import com.devision.applicant.dto.ResumeUpdateRequest;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Resume;

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
}
