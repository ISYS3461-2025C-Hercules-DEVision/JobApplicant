package com.devision.subscription.dto;

import com.devision.subscription.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response shape returned by Search Profile endpoints. Values are normalized
 * and safe for direct rendering by clients.
 */
public class SearchProfileResponse {
    public String applicantId;
    public List<String> technicalTags;
    public List<EmploymentStatus> employmentStatuses;
    public String country;
    public BigDecimal minSalary;
    public BigDecimal maxSalary;
    public List<String> desiredJobTitles;

    public SearchProfileResponse(String applicantId,
            List<String> technicalTags,
            List<EmploymentStatus> employmentStatuses,
            String country,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            List<String> desiredJobTitles) {
        this.applicantId = applicantId;
        this.technicalTags = technicalTags;
        this.employmentStatuses = employmentStatuses;
        this.country = country;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.desiredJobTitles = desiredJobTitles;
    }
}
