package com.devision.application.dto.internal.command;

public class CreateApplicationCommand {
    private String applicantId;
    private String jobPostId;
    private String companyId;
    private String coverLetterText;

    public CreateApplicationCommand(String applicantId, String jobPostId, String companyId, String coverLetterText) {
        this.applicantId = applicantId;
        this.jobPostId = jobPostId;
        this.companyId = companyId;
        this.coverLetterText = coverLetterText;
    }

    public String getApplicantId() { return applicantId; }
    public String getJobPostId() { return jobPostId; }
    public String getCompanyId() { return companyId; }
    public String getCoverLetterText() { return coverLetterText; }
}
