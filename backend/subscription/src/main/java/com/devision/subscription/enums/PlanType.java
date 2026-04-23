package com.devision.subscription.enums;

/**
 * ============================================================
 * PLAN TYPE ENUM - SUBSCRIPTION TIER DEFINITIONS
 * ============================================================
 *
 * PURPOSE:
 * - Defines available subscription tiers.
 * - Used in Subscription and SubscriptionStatusResponse.
 * - Drives feature access control in frontend and backend.
 *
 * VALUES:
 *
 * FREE
 * - Default plan for all new applicants.
 * - No expiry (expiryDate = null in Subscription).
 * - Features: Basic job search, profile viewing, limited applications.
 * - Cost: $0 (always active unless explicitly downgraded).
 * - Duration: Indefinite.
 * - Transition: Created when applicant first signs up; also created on
 * subscription cancellation.
 *
 * PREMIUM
 * - Paid tier; requires Stripe checkout.
 * - Cost: $10.00 (fixed; set in SubscriptionServiceImpl).
 * - Duration: 30 days from purchase date (expiryDate = startDate + 30 days).
 * - Features: Advanced job filters, priority applications, employer messaging
 * (tbd).
 * - Renewal: Manual; user must repurchase after expiry.
 * - Auto-downgrade: NOT implemented; applicant retains PREMIUM until manually
 * cancelled.
 *
 * TRANSITIONS:
 *
 * FREE -> PREMIUM:
 * 1. User clicks "Subscribe Now" button.
 * 2. Redirected to Stripe Checkout.
 * 3. Completes payment.
 * 4. Stripe redirects to /subscription/return?session_id=...
 * 5. Frontend calls /complete; backend:
 * - Deactivates existing FREE subscription.
 * - Creates new PREMIUM (startDate=now, expiryDate=now+30d, active=true).
 * 6. Frontend refreshes; user sees PREMIUM status and expiry date.
 *
 * PREMIUM -> FREE:
 * 1. User clicks "Cancel Subscription" button (only shown for PREMIUM).
 * 2. Confirms cancellation.
 * 3. Frontend calls POST /{applicantId}/cancel.
 * 4. Backend:
 * - Deactivates all active subscriptions (including PREMIUM).
 * - Creates new FREE subscription (active=true, expiryDate=null).
 * 5. Frontend emits subscription-updated event; UI refreshes.
 * 6. User sees FREE status and "Subscribe Now" button.
 *
 * STORAGE:
 * - Persisted in Subscription.planType (database field).
 * - Serialized in SubscriptionStatusResponse and PaymentInitiateResponseDTO.
 * - Enum values: FREE, PREMIUM (no other states).
 *
 * USAGE IN CODE:
 * - service.getMySubscription() returns current plan (FREE or PREMIUM).
 * - service.initiatePayment() creates PREMIUM-bound transaction.
 * - service.completePayment() activates PREMIUM subscription.
 * - service.cancelSubscription() sets plan to FREE.
 *
 * FRONTEND LOGIC:
 * - if (subscriptionStatus.planType === "PREMIUM" && subscriptionStatus.active)
 * {
 * show "Cancel Subscription" button with expiry countdown.
 * } else {
 * show "Subscribe Now" button (redirects to Stripe).
 * }
 * ============================================================
 */
public enum PlanType {
    /**
     * Free tier; no expiry; default plan.
     */
    FREE,

    /**
     * Premium tier; 30-day expiry; requires payment.
     */
    PREMIUM
}