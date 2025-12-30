package com.devision.application.dto.external.response;

import com.devision.application.enums.ApplicationStatus;

import java.time.Instant;

public class ApplicationSummaryResponse {
    private String ApplicationId;
    private String jobPostId;
    private String companyId;
    private ApplicationStatus status;
    private Instant createdAt;

    public String getApplicationId() { return ApplicationId; }
    public void setApplicationId(String ApplicationId) { this.ApplicationId = ApplicationId; }

    public String getJobPostId() { return jobPostId; }
    public void setJobPostId(String jobPostId) { this.jobPostId = jobPostId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
