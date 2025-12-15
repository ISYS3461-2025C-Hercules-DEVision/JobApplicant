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
public class Education {

    private String educationId;       // UUID
    private String applicantId;       // UUID

    private String institution;
    private String degree;
    private Integer fromYear;
    private Integer toYear;
    private Double gpa;               // nullable

    private Instant createdAt;
    private Instant updatedAt;
}
