package com.devision.subscription.model;

import com.devision.subscription.enums.EmploymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "search_profiles")
public class SearchProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String applicantId;

    private List<String> technicalTags; // e.g., Kafka, React, Spring Boot
    private List<EmploymentStatus> employmentStatuses; // multiple selections
    private String country;
    private Integer minSalary; // USD
    private Integer maxSalary; // USD, null means no upper limit
    private List<String> desiredJobTitles; // parsed from semicolon-separated string

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public List<String> getTechnicalTags() {
        return technicalTags;
    }

    public void setTechnicalTags(List<String> technicalTags) {
        this.technicalTags = technicalTags;
    }

    public List<EmploymentStatus> getEmploymentStatuses() {
        return employmentStatuses;
    }

    public void setEmploymentStatuses(List<EmploymentStatus> employmentStatuses) {
        this.employmentStatuses = employmentStatuses;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public List<String> getDesiredJobTitles() {
        return desiredJobTitles;
    }

    public void setDesiredJobTitles(List<String> desiredJobTitles) {
        this.desiredJobTitles = desiredJobTitles;
    }
}
