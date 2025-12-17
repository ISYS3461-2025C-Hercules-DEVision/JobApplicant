package com.devision.applicant.dto.applicant;

import com.devision.applicant.model.Education;
import com.devision.applicant.model.WorkExperience;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApplicantProfileRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String country;

    private String profileImageUrl;

    @Size(max = 1000)
    private String objectiveSummary;

    private List<Education> educations = new ArrayList<>();
    private List<WorkExperience> experiences = new ArrayList<>();
}
