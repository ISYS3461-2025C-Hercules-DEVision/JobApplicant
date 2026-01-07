package com.devision.application.kafka.event;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.model.Application;
import com.devision.application.kafka.event.ApplicationEventType;

import java.time.Instant;

/**
 * Kafka event payload for Application service.
 * This is a simple DTO that can be serialized to JSON by Spring Kafka.
 */
public class ApplicationEvent {

    private ApplicationEventType eventType;          // e.g. APPLICATION_CREATED, CV_UPLOADED
    private String applicationId;
    private String applicantId;
    private String jobPostId;
    private String companyId;
    private ApplicationStatus status;
    private Instant occurredAt;

    public ApplicationEvent() {}

    public ApplicationEvent(ApplicationEventType eventType,
                            String applicationId,
                            String applicantId,
                            String jobPostId,
                            String companyId,
                            ApplicationStatus status,
                            Instant occurredAt) {
        this.eventType = eventType;
        this.applicationId = applicationId;
        this.applicantId = applicantId;
        this.jobPostId = jobPostId;
        this.companyId = companyId;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    // -------- factory methods (match your service calls) --------

    public static ApplicationEvent created(Application app) {
        return new ApplicationEvent(
                ApplicationEventType.APPLICATION_CREATED,
                app.getApplicationId(),
                app.getApplicantId(),
                app.getJobPostId(),
                app.getCompanyId(),
                app.getStatus(),
                Instant.now()
        );
    }

    public static ApplicationEvent cvUploaded(Application app) {
        return new ApplicationEvent(
                ApplicationEventType.CV_UPLOADED,
                app.getApplicationId(),
                app.getApplicantId(),
                app.getJobPostId(),
                app.getCompanyId(),
                app.getStatus(),
                Instant.now()
        );
    }

    public static ApplicationEvent coverLetterUploaded(Application app) {
        return new ApplicationEvent(
                ApplicationEventType.COVER_LETTER_UPLOADED,
                app.getApplicationId(),
                app.getApplicantId(),
                app.getJobPostId(),
                app.getCompanyId(),
                app.getStatus(),
                Instant.now()
        );
    }

    // -------- getters --------

    public ApplicationEventType getEventType() { return eventType; }
    public String getApplicationId() { return applicationId; }
    public String getApplicantId() { return applicantId; }
    public String getJobPostId() { return jobPostId; }
    public String getCompanyId() { return companyId; }
    public ApplicationStatus getStatus() { return status; }
    public Instant getOccurredAt() { return occurredAt; }
}
