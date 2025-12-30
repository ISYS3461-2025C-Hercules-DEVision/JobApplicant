package com.devision.application.dto.internal.command;

public class CreateApplicationCommand {
    private String applicantId;
    private String jobPostId;
    private String companyId;

    public CreateApplicationCommand(String applicantId, String jobPostId, String companyId) {
        this.applicantId = applicantId;
        this.jobPostId = jobPostId;
        this.companyId = companyId;
    }

    public String getApplicantId() { return applicantId; }
    public String getJobPostId() { return jobPostId; }
    public String getCompanyId() { return companyId; }
}
