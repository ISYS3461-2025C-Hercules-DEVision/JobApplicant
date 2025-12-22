package com.devision.application.dto.external.response;

import java.time.Instant;

public class ApplicationResponse {

    private String id;
    private String applicantId;
    private String jobPostId;
    private String companyId;

    private String status; // SUBMITTED/...
    private String coverLetterText;

    private FileRefResponse cv;
    private FileRefResponse coverLetterFile;

    private Instant createdAt;
    private Instant updatedAt;

    public static class FileRefResponse {
        private String publicId;
        private String url;
        private String originalFileName;
        private String contentType;
        private long sizeBytes;

        public String getPublicId() { return publicId; }
        public void setPublicId(String publicId) { this.publicId = publicId; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getOriginalFileName() { return originalFileName; }
        public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }

        public long getSizeBytes() { return sizeBytes; }
        public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getJobPostId() { return jobPostId; }
    public void setJobPostId(String jobPostId) { this.jobPostId = jobPostId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCoverLetterText() { return coverLetterText; }
    public void setCoverLetterText(String coverLetterText) { this.coverLetterText = coverLetterText; }

    public FileRefResponse getCv() { return cv; }
    public void setCv(FileRefResponse cv) { this.cv = cv; }

    public FileRefResponse getCoverLetterFile() { return coverLetterFile; }
    public void setCoverLetterFile(FileRefResponse coverLetterFile) { this.coverLetterFile = coverLetterFile; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
