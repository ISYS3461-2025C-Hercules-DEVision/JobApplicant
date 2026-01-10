package com.devision.application.dto.internal.view;

import com.devision.application.enums.ApplicationStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class ApplicationSummaryView {
    private String applicationId;
    private String jobPostId;
    private String companyId;
    private ApplicationStatus status;
    private Instant createdAt;
}
