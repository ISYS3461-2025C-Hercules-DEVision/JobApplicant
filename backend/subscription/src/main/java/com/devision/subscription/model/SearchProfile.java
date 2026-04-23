package com.devision.subscription.model; // Model package

import com.devision.subscription.enums.EmploymentStatus; // Employment status enum
import org.springframework.data.annotation.Id; // Mongo document id
import org.springframework.data.mongodb.core.index.Indexed; // Index annotation
import org.springframework.data.mongodb.core.mapping.Document; // Document annotation

import java.math.BigDecimal; // Salary values
import java.util.List; // Lists of values

/**
 * Applicant-configured preferences for job matching and notifications.
 * Upserted via REST and evaluated against incoming job-post events.
 */
@Document(collection = "search_profiles") // Mongo collection name
public class SearchProfile { // Search profile document

    @Id // Document primary key
    private String id; // Document id

    @Indexed(unique = true) // Unique index per applicant
    private String applicantId; // Applicant identifier

    private List<String> technicalTags; // e.g., Kafka, React, Spring Boot
    private List<EmploymentStatus> employmentStatuses; // multiple selections
    private String country; // Desired country
    private BigDecimal minSalary; // USD minimum salary
    private BigDecimal maxSalary; // USD maximum salary, null means no upper limit
    private List<String> desiredJobTitles; // Parsed from semicolon-separated string

    public String getId() { // Get id
        return id; // Return id
    }

    public void setId(String id) { // Set id
        this.id = id; // Assign id
    }

    public String getApplicantId() { // Get applicant id
        return applicantId; // Return applicant id
    }

    public void setApplicantId(String applicantId) { // Set applicant id
        this.applicantId = applicantId; // Assign applicant id
    }

    public List<String> getTechnicalTags() { // Get technical tags
        return technicalTags; // Return tags
    }

    public void setTechnicalTags(List<String> technicalTags) { // Set technical tags
        this.technicalTags = technicalTags; // Assign tags
    }

    public List<EmploymentStatus> getEmploymentStatuses() { // Get employment statuses
        return employmentStatuses; // Return statuses
    }

    public void setEmploymentStatuses(List<EmploymentStatus> employmentStatuses) { // Set statuses
        this.employmentStatuses = employmentStatuses; // Assign statuses
    }

    public String getCountry() { // Get country
        return country; // Return country
    }

    public void setCountry(String country) { // Set country
        this.country = country; // Assign country
    }

    public BigDecimal getMinSalary() { // Get min salary
        return minSalary; // Return min salary
    }

    public void setMinSalary(BigDecimal minSalary) { // Set min salary
        this.minSalary = minSalary; // Assign min salary
    }

    public BigDecimal getMaxSalary() { // Get max salary
        return maxSalary; // Return max salary
    }

    public void setMaxSalary(BigDecimal maxSalary) { // Set max salary
        this.maxSalary = maxSalary; // Assign max salary
    }

    public List<String> getDesiredJobTitles() { // Get desired titles
        return desiredJobTitles; // Return titles
    }

    public void setDesiredJobTitles(List<String> desiredJobTitles) { // Set desired titles
        this.desiredJobTitles = desiredJobTitles; // Assign titles
    }
}
