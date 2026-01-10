package com.example.application.application.model;

import com.example.application.application.enums.ApplicationStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;





import java.time.Instant;
import java.time.LocalDate;
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
    private List<FileReference> documents = new ArrayList<>();
    private Instant createdAt;
    private Boolean isArchived = false;
    private Instant deletedAt;            // nullable
}
