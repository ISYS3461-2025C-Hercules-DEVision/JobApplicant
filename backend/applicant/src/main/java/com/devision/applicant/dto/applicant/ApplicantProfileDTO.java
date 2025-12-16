package com.devision.applicant.dto.applicant;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.WorkExperience;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ApplicantProfileDTO {
    @Email
    private String email;
    private String password;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String country;
    private String objectiveSummary;

    private List<Education> educations;
    private List<WorkExperience> experiences;
    private Instant createdAt;
    private Instant updatedAt;
}
