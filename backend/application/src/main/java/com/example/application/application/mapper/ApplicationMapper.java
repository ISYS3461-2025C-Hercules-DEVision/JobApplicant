package com.example.application.application.mapper;



import com.example.application.application.dto.ApplicationCreateRequest;
import com.example.application.application.dto.ApplicationDTO;
import com.example.application.application.dto.UpdateStatusRequest;
import com.example.application.application.enums.ApplicationStatus;
import com.example.application.application.model.Application;

import java.time.Instant;

public class ApplicationMapper {

    private ApplicationMapper(){}

    public static Application toEntity(ApplicationCreateRequest request){
        Application application = new Application();
        application.setApplicationId(request.applicantId());
        application.setJobPostId(request.jobPostId());
        application.setCompanyId(request.companyId());
        application.setDocuments(request.documents());
        application.setStatus(ApplicationStatus.PENDING);

        Instant now = Instant.now();
        application.setSubmissionDate(now);
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        return application;
    }

    public static void updateEntity(Application application, UpdateStatusRequest request){
        if(request.status() != null){
            application.setStatus(request.status());
        }

        application.setUpdatedAt(Instant.now());
    }

    public static ApplicationDTO toDto(Application application){
        return new ApplicationDTO(
                application.getApplicationId(),
                application.getApplicantId(),
                application.getJobPostId(),
                application.getCompanyId(),
                application.getStatus(),
                application.getSubmissionDate(),
                application.getUpdatedAt(),
                application.getFeedback(),
                application.getDocuments() != null ? application.getDocuments() : null,
                application.getCreatedAt(),
                Boolean.TRUE.equals(application.getIsArchived())
        );
    }

}
