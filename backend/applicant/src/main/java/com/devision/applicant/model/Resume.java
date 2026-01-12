package com.devision.applicant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field(name = "resumeId")
    @Id
    private String resumeId;

    @Field(name = "applicantId")
    private String applicantId; //FK

    @Field(name = "headline")
    private String headline;

    @Field(name = "objective")
    private String objective;

    @Field(name = "education")
    private List<Education> education = new ArrayList<>();       // embedded

    @Field(name = "experience")
    private List<WorkExperience> experience = new ArrayList<>(); // embedded

    @Field(name = "skills")
    private List<String> skills;

    @Field(name = "certifications")
    private List<String> certifications;

    @Builder.Default
    @Field(name = "mediaPortfolios")
    private List<MediaPortfolio> mediaPortfolios = new ArrayList<>();

    @Field(name = "updatedAt")
    private Instant updatedAt;

    @Field(name = "minSalary")
    private BigDecimal minSalary;

    @Field(name = "maxSalary")
    private BigDecimal maxSalary;

    private LocalDateTime deletedAt;
}
