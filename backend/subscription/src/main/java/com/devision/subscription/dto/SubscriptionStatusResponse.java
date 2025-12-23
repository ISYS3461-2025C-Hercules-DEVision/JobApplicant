package com.devision.subscription.dto;

import java.time.Instant;

public class SubscriptionStatusResponse {

    private String planType;
    private boolean isActive;
    private Instant expiryDate;

    public SubscriptionStatusResponse(
            String planType,
            boolean isActive,
            Instant expiryDate
    ) {
        this.planType = planType;
        this.isActive = isActive;
        this.expiryDate = expiryDate;
    }

    public String getPlanType() {
        return planType;
    }

    public boolean isActive() {
        return isActive;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }
}
