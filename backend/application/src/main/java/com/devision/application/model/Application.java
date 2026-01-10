package com.devision.application.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.devision.application.enums.ApplicationStatus;  

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applications")
public class Application {

    @Id
    private String applicationId;     // UUID

    private String applicantId;       // UUID
    private String jobPostId;         // from JM
    private String companyId;         // from JM

    private ApplicationStatus status;

    private Instant submissionDate;
    private Instant updatedAt;

    private String feedback;         // nullable

    // File references
    private FileReference applicantCV;     // CVFileReference | nullable
    private FileReference coverLetter;     // CoverLetterReference | nullable

    private Instant createdAt;
    private Boolean isArchived = false;
    private Instant deletedAt;            // nullable
}
