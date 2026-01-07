package com.devision.application.dto.internal.view;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;

import java.time.Instant;

public class ApplicationView {
    private String applicationId;
    private String applicantId;
    private String jobPostId;
    private String companyId;
    private ApplicationStatus status;

    private FileView applicantCV;
    private FileView coverLetter;

    private Instant createdAt;
    private Instant updatedAt;

    public static class FileView {
        private String fileId;
        private String fileUrl;
        private String publicId;
        private FileType fileType;
        private Instant createdAt;
        private Instant updatedAt;

        public FileView() {}

        public FileView(String fileId, String fileUrl, String publicId,
                        FileType fileType, Instant createdAt, Instant updatedAt) {
            this.fileId = fileId;
            this.fileUrl = fileUrl;
            this.publicId = publicId;
            this.fileType = fileType;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getFileId() { return fileId; }
        public String getFileUrl() { return fileUrl; }
        public String getPublicId() { return publicId; }
        public FileType getFileType() { return fileType; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getUpdatedAt() { return updatedAt; }
    }

    public String getApplicationId() { return applicationId; }
    public String getApplicantId() { return applicantId; }
    public String getJobPostId() { return jobPostId; }
    public String getCompanyId() { return companyId; }
    public ApplicationStatus getStatus() { return status; }
    public FileView getApplicantCV() { return applicantCV; }
    public FileView getCoverLetter() { return coverLetter; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
