package com.devision.application.dto.external.request;

import jakarta.validation.constraints.NotBlank;

public class CreateApplicationRequest {

    @NotBlank
    private String jobPostId;

    @NotBlank
    private String companyId;

    // optional
    private String coverLetterText;

    public String getJobPostId() { return jobPostId; }
    public void setJobPostId(String jobPostId) { this.jobPostId = jobPostId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCoverLetterText() { return coverLetterText; }
    public void setCoverLetterText(String coverLetterText) { this.coverLetterText = coverLetterText; }
}
