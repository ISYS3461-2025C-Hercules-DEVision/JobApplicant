package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "search_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProfile {
    @Id
    private String searchProfileId = UUID.randomUUID().toString(); //PK

    private String applicantId; //FK to applicant
    private String profileName;
    private String desiredCountry;
    private Integer desiredMinSalary;
    private Integer desiredMaxSalary;
    private List<String> jobTitles = new ArrayList<>();
    private List<String> technicalBackground = new ArrayList<>();
    private List<String> employmentStatus = new ArrayList<>();
    private Boolean isActive = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
