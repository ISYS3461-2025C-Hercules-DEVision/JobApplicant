package com.devision.subscription.dto;

import com.devision.subscription.enums.PlanType;
import java.time.Instant;

/**
 * ============================================================
 * SUBSCRIPTION STATUS RESPONSE - API RESPONSE DTO
 * ============================================================
 *
 * PURPOSE:
 * - Serializable response object for subscription queries.
 * - Sent by backend to frontend for /profile, /subscription page displays.
 * - Contains only essential subscription info (not sensitive payment data).
 *
 * JSON EXAMPLE:
 * {
 * "planType": "PREMIUM",
 * "active": true,
 * "expiryDate": "2026-02-13T08:00:00Z"
 * }
 *
 * USAGE ENDPOINTS:
 * - GET /api/v1/subscriptions/{applicantId}: Returns current status.
 * - POST /api/v1/subscriptions/{applicantId}/cancel: Returns updated status
 * (FREE).
 * - POST /api/v1/subscriptions/{applicantId}/default: Returns status (FREE if
 * created).
 *
 * FIELDS:
 * - planType: PlanType enum {FREE, PREMIUM}.
 * - FREE: No premium features; default plan.
 * - PREMIUM: Full features; 30-day expiry.
 * 
 * - active: Boolean; true if subscription is currently valid.
 * - true: User has access to features matching planType.
 * - false: Subscription is historical/inactive (should not be returned unless
 * querying history).
 * 
 * - expiryDate: Instant (ISO 8601 timestamp) when subscription expires.
 * - For PREMIUM: Future date (e.g., 2026-02-13T08:00:00Z).
 * - For FREE: null (no expiry).
 * - After expiry: Subscription deactivates (not auto-downgraded; requires
 * separate logic).
 *
 * FRONTEND USAGE:
 * 1. Profile Header: Displays "Premium • Expires Jan 13, 2026".
 * 2. Subscription Page: Shows plan type and expiry; toggle button label.
 * 3. Conditional rendering:
 * if (planType === "PREMIUM" && active) {
 * display "Cancel Subscription" button;
 * } else {
 * display "Subscribe Now" button;
 * }
 *
 * CONSTRUCTION:
 * - From Subscription entity:
 * new SubscriptionStatusResponse(sub.getPlanType(), sub.isActive(),
 * sub.getExpiryDate())
 * - Default (no active subscription):
 * new SubscriptionStatusResponse(PlanType.FREE, false, null)
 * ============================================================
 */
public class SubscriptionStatusResponse {

    private PlanType planType;
    private boolean active;
    private Instant expiryDate;

    public SubscriptionStatusResponse(
            PlanType planType,
            boolean active,
            Instant expiryDate) {
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
