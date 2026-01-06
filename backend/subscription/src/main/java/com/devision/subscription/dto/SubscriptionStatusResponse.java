package com.devision.subscription.dto;

import com.devision.subscription.enums.PlanType;
import java.time.Instant;

public class SubscriptionStatusResponse {

    private PlanType planType;
    private boolean active;
    private Instant expiryDate;

    public SubscriptionStatusResponse(
            PlanType planType,
            boolean active,
            Instant expiryDate
    ) {
        this.planType = planType;
        this.active = active;
        this.expiryDate = expiryDate;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }
}
