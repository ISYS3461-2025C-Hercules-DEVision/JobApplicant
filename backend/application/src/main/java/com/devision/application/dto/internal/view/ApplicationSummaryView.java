package com.devision.application.dto.internal.view;

import com.devision.application.enums.ApplicationStatus;

import java.time.Instant;

public class ApplicationSummaryView {
    public String id;
    public String jobPostId;
    public String companyId;
    public ApplicationStatus status;
    public Instant createdAt;
}
