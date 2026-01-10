package com.example.application.application.dto;



import com.example.application.application.enums.ApplicationStatus;
import com.example.application.application.model.FileReference;

import java.time.Instant;
import java.util.List;


public record ApplicationDTO(
        String applicationId,
        String applicantId,
        String jobPostId,
        String companyId,
        ApplicationStatus status,
        Instant submissionDate,
        Instant updatedAt,
        String feedback,
        List<FileReference> documents,      // URL to CV file
        Instant createdAt,
        boolean isArchived) {

}
