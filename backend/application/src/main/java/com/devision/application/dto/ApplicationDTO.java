package com.devision.application.dto;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.model.FileReference;

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
