package com.devision.application.dto.internal.command;

import org.springframework.web.multipart.MultipartFile;

public class UploadCoverLetterCommand {
    private final String applicantId;
    private final String applicationId;
    private final MultipartFile file;

    public UploadCoverLetterCommand(String applicantId, String applicationId, MultipartFile file) {
        this.applicantId = applicantId;
        this.applicationId = applicationId;
        this.file = file;
    }

    public String getApplicantId() { return applicantId; }
    public String getApplicationId() { return applicationId; }
    public MultipartFile getFile() { return file; }
}
