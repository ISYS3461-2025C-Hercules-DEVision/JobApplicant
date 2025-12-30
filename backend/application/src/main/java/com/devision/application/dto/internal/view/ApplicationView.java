package com.devision.application.dto.internal.view;
import com.devision.application.enums.FileType;
import com.devision.application.enums.ApplicationStatus;

import java.time.Instant;

public class ApplicationView {
    public String applicantId;
    public String jobPostId;
    public String companyId;
    public ApplicationStatus status;

    public FileView applicantCV;
    public FileView coverLetter;

    public Instant createdAt;
    public Instant updatedAt;

    public static class FileView {
        public String fileId;
        public String fileUrl;
        public FileType fileType;
        public Instant createdAt;
    }
}
