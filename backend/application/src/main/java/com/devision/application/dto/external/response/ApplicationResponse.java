package com.devision.application.dto.external.response;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;

import java.time.Instant;

public class ApplicationResponse {

    private String applicationId;

    private String applicantId;
    private String jobPostId;
    private String companyId;

    private ApplicationStatus status;

    private FileRefResponse applicantCV;
    private FileRefResponse coverLetter;

    private Instant createdAt;
    private Instant updatedAt;

    public static class FileRefResponse {
        private String fileId;
        private String fileUrl;
        private String publicId;
        private FileType fileType;
        private Instant createdAt;
        private Instant updatedAt;

        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }

        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

        public String getPublicId() { return publicId; }
        public void setPublicId(String publicId) { this.publicId = publicId; }

        public FileType getFileType() { return fileType; }
        public void setFileType(FileType fileType) { this.fileType = fileType; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getJobPostId() { return jobPostId; }
    public void setJobPostId(String jobPostId) { this.jobPostId = jobPostId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public FileRefResponse getApplicantCV() { return applicantCV; }
    public void setApplicantCV(FileRefResponse applicantCV) { this.applicantCV = applicantCV; }

    public FileRefResponse getCoverLetter() { return coverLetter; }
    public void setCoverLetter(FileRefResponse coverLetter) { this.coverLetter = coverLetter; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
