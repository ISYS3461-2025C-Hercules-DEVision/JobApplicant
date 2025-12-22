package com.devision.application.dto.internal.view;

import java.time.Instant;

public class ApplicationView {
    public String id;
    public String applicantId;
    public String jobPostId;
    public String companyId;
    public String status;
    public String coverLetterText;

    public FileView cv;
    public FileView coverLetterFile;

    public Instant createdAt;
    public Instant updatedAt;

    public static class FileView {
        public String publicId;
        public String url;
        public String originalFileName;
        public String contentType;
        public long sizeBytes;
    }
}
