package com.devision.applicant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_profiles")
public class SearchProfile {

    @Id
    private String searchProfileId; // UUID

    private String applicantId; // FK-like reference

    private String profileName;
    private String desiredCountry;
    private String desiredCity;

    private Double desiredMinSalary;
    private Double desiredMaxSalary;

    private List<String> jobTitles;
    private List<String> technicalBackground;
    private List<String> employmentStatus;

    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}
