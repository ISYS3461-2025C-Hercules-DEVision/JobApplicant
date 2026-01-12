package com.devision.application.mapper;

import com.devision.application.dto.ApplicationCreateRequest;
import com.devision.application.dto.ApplicationDTO;
import com.devision.application.dto.UpdateStatusRequest;
import com.devision.application.enums.ApplicationStatus;
import com.devision.application.model.Application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class ApplicationMapper {

    private ApplicationMapper() {}

    public static Application toEntity(ApplicationCreateRequest request) {
        Instant now = Instant.now();

        Application application = new Application();
        application.setApplicationId(UUID.randomUUID().toString());

        application.setApplicantId(request.applicantId());
        application.setJobPostId(request.jobPostId());
        application.setCompanyId(request.companyId());

        // null-safe documents
        application.setDocuments(request.documents() != null ? request.documents() : new ArrayList<>());

        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmissionDate(now);
        application.setCreatedAt(now);
        application.setUpdatedAt(now);
        application.setIsArchived(false);

        return application;
    }

    public static void updateEntity(Application application, UpdateStatusRequest request) {
        if (request.status() != null) {
            application.setStatus(request.status());
        }
        application.setUpdatedAt(Instant.now());
    }

    public static ApplicationDTO toDto(Application application) {
        return new ApplicationDTO(
                application.getApplicationId(),
                application.getApplicantId(),
                application.getJobPostId(),
                application.getCompanyId(),
                application.getStatus(),
                application.getSubmissionDate(),
                application.getUpdatedAt(),
                application.getFeedback(),
                application.getDocuments(), // ok if null, or wrap Optional if you want
                application.getCreatedAt(),
                Boolean.TRUE.equals(application.getIsArchived())
        );
    }
}
