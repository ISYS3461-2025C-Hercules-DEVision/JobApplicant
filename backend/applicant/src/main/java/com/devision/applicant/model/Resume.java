package com.devision.applicant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("resumes")
public class Resume {
  @Id
  private String resumeId;
  private String applicantId;

  private String headline;
  private String objective;

  private List<Education> education;       // embedded
  private List<WorkExperience> experience; // embedded

  private List<String> skills;
  private List<String> certifications;

  private Instant createdAt;
  private Instant updatedAt;
}

