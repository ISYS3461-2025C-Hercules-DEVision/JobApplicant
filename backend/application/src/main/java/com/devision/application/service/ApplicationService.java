package com.devision.application.service;

import com.devision.application.dto.ApplicationCreateRequest;
import com.devision.application.dto.ApplicationDTO;
import com.devision.application.dto.CompanyApplicationViewDTO;
import com.devision.application.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    List<ApplicationDTO> getApplicationsByApplicantId(String applicantId);
    ApplicationDTO getById(String applicationId);
    ApplicationDTO createApplication(ApplicationCreateRequest req);
    ApplicationDTO updateStatus(String applicationId, ApplicationStatus newStatus);
    List<CompanyApplicationViewDTO>getApplicationsForJobPost(String companyId, String jobPostId);
    void updateApplicationStatus(String jobPostId, String newStatus, String feedback);
}
