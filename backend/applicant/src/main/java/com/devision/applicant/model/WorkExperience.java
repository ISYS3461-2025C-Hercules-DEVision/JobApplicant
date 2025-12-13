package com.devision.applicant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperience {

    private String workExpId;         // UUID
    private String applicantId;       // UUID

    private String jobTitle;
    private String companyName;
    private String fromYear;          // or LocalDate
    private String toYear;            // nullable if current
    private String description;

    private Instant createdAt;
    private Instant updatedAt;
}
