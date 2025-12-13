package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    @Id
    private String resumeId = UUID.randomUUID().toString();

    private String applicantId; //FK to Applicant
    private String headline;
    private String objective;

    @Builder.Default
    @Field("educations")
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    @Field("experiences")
    private List<Experience> experiences = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<String> certifications = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
