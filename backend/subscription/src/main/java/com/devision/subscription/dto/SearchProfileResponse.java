package com.devision.subscription.dto; // DTO package

import com.devision.subscription.enums.EmploymentStatus; // Employment status enum

import java.math.BigDecimal; // Salary values
import java.util.List; // Lists of values

/**
 * Response shape returned by Search Profile endpoints. Values are normalized
 * and safe for direct rendering by clients.
 */
public class SearchProfileResponse { // Response payload for search profile
    public String applicantId; // Applicant identifier
    public List<String> technicalTags; // Normalized technical tags
    public List<EmploymentStatus> employmentStatuses; // Normalized statuses
    public String country; // Country
    public BigDecimal minSalary; // Minimum salary
    public BigDecimal maxSalary; // Maximum salary
    public List<String> desiredJobTitles; // Desired job titles

    public SearchProfileResponse(String applicantId, // Applicant identifier
            List<String> technicalTags, // Tags list
            List<EmploymentStatus> employmentStatuses, // Status list
            String country, // Country
            BigDecimal minSalary, // Min salary
            BigDecimal maxSalary, // Max salary
            List<String> desiredJobTitles) { // Job titles
        this.applicantId = applicantId; // Assign applicant id
        this.technicalTags = technicalTags; // Assign tags
        this.employmentStatuses = employmentStatuses; // Assign statuses
        this.country = country; // Assign country
        this.minSalary = minSalary; // Assign min salary
        this.maxSalary = maxSalary; // Assign max salary
        this.desiredJobTitles = desiredJobTitles; // Assign titles
    }
} // End DTO
