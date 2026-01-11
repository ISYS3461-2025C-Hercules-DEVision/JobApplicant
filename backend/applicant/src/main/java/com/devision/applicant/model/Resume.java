package com.devision.applicant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resumes")
public class Resume {
    @Id
    private String resumeId;
    private String applicantId; //FK

    private String headline;
    private String objective;

    private List<Education> education = new ArrayList<>();       // embedded
    private List<WorkExperience> experience = new ArrayList<>(); // embedded

    private List<String> skills;
    private List<String> certifications;

    @Builder.Default
    private List<MediaPortfolio> mediaPortfolios = new ArrayList<>();

    private Instant updatedAt;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;

    private LocalDateTime deletedAt;
}
