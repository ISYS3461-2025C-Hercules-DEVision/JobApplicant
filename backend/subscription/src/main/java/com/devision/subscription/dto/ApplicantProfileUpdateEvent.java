package com.devision.subscription.dto;

import java.time.Instant;

public class ApplicantProfileUpdateEvent {
    private String applicantId;
    private Integer minSalary;
    private Integer maxSalary;
    private Instant updatedAt;
    private String source;
    private String eventType;

    public ApplicantProfileUpdateEvent() {
    }

    public ApplicantProfileUpdateEvent(String applicantId, Integer minSalary, Integer maxSalary, Instant updatedAt) {
        this.applicantId = applicantId;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.updatedAt = updatedAt;
        this.source = "subscription";
        this.eventType = "PROFILE_SALARY_UPDATED";
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
