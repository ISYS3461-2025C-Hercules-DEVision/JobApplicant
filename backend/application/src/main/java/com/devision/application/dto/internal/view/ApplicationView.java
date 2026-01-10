package com.devision.application.dto.internal.view;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileView {
        private String fileId;
        private String fileUrl;
        private String publicId;
        private FileType fileType;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
