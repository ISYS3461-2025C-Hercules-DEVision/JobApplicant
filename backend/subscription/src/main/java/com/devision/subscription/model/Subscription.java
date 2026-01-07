package com.devision.subscription.model;

import com.devision.subscription.enums.PlanType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String subscriptionId;
    private String applicantId;
    private PlanType planType;
    private Instant startDate;
    private Instant expiryDate;
    private boolean isActive;

    public Subscription() {}

    public Subscription(String applicantId, PlanType planType,
                        Instant startDate, Instant expiryDate, boolean isActive) {
        this.applicantId = applicantId;
        this.planType = planType;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
    }

    public String getSubscriptionId() { return subscriptionId; }
    public String getApplicantId() { return applicantId; }
    public PlanType getPlanType() { return planType; }
    public Instant getStartDate() { return startDate; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isActive() { return isActive; }

    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
    public void setActive(boolean active) { isActive = active; }
}
