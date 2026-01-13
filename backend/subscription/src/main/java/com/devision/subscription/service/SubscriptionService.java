package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;

/**
 * Business operations for applicant subscriptions and payment initiation.
 */
public interface SubscriptionService {

    /** Returns current subscription status for the given applicant. */
    SubscriptionStatusResponse getMySubscription(String applicantId);

    /**
     * Initiates payment for a subscription. When forwarding is disabled, this
     * records a mock SUCCESS and creates a PREMIUM subscription; otherwise it
     * forwards to JM and records a CREATED transaction for reconciliation.
     */
    PaymentInitiateResponseDTO createMockPayment(String applicantId, String email, String authBearer);

    /** Completes payment for a subscription using the provided session ID. */
    void completePayment(String sessionId);

    /** Ensures a FREE active subscription exists and returns the status. */
    SubscriptionStatusResponse createDefaultSubscriptionForUser(String applicantId);
}
