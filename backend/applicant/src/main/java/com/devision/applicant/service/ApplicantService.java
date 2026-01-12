package com.devision.applicant.service;

import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.MediaPortfolio;

import java.util.List;

public interface ApplicantService {
    ApplicantDTO create(ApplicantCreateRequest request);

    ApplicantDTO getById(String id);

    List<ApplicantDTO> getAll();

    ApplicantDTO update(String id, ApplicantUpdateRequest request);

    void delete(String id);

    ApplicantDTO uploadProfileImage(String id, UploadAvatarRequest request);

    MediaPortfolio uploadMediaPortfolio(String applicantId, UploadMediaPortfolioRequest request);

    List<MediaPortfolio> getMediaPortfolio(String applicantId, Visibility visibility);

    void deleteMediaPortfolio(String applicantId, String mediaId);

    ApplicantDTO deactivateApplicantAccount(String applicantId);

    ApplicantDTO activateApplicantAccount(String applicantId);

    ResumeDTO updateResume(String applicantId, ResumeUpdateRequest request);

    ResumeDTO getResume(String applicantId);

    void deleteResume(String applicantId);

    List<ResumeDTO> getAllResumes();


}
