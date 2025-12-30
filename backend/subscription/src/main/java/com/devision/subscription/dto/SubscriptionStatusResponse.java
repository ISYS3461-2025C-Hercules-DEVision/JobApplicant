package com.devision.subscription.dto;

import java.time.Instant;

public class SubscriptionStatusResponse {

    private String planType;
    private boolean active;
    private Instant expiryDate;

    public SubscriptionStatusResponse() {}

    public SubscriptionStatusResponse(String planType, boolean active, Instant expiryDate) {
        this.planType = planType;
        this.active = active;
        this.expiryDate = expiryDate;
    }

    public String getPlanType() { return planType; }
    public boolean isActive() { return active; }
    public Instant getExpiryDate() { return expiryDate; }
}
