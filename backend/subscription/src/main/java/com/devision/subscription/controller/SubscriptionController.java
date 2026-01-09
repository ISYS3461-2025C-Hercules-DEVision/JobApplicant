package com.devision.subscription.controller;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for managing applicant subscriptions.
 *
 * Routes are served behind the API Gateway under /api/v1/subscriptions.
 * - GET /{applicantId}: Current subscription status for the applicant
 * - POST /{applicantId}/checkout: Create a mock payment or forward to JM
 * - POST /{applicantId}/default: Create a FREE active subscription when missing
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Returns the current subscription of an applicant. If none exists, a
     * FREE/inactive default is returned by the service.
     */
    @GetMapping("/{applicantId}")
    public SubscriptionStatusResponse getMySubscription(
            @PathVariable String applicantId) {
        return subscriptionService.getMySubscription(applicantId);
    }

    /**
     * Initiates a subscription payment for the applicant.
     * When forwarding is disabled, a mock SUCCESS payment is recorded and a
     * PREMIUM subscription is created. When enabled, the request is forwarded
     * to JM and a CREATED transaction is stored locally.
     */
    @PostMapping("/{applicantId}/checkout")
    public PaymentInitiateResponseDTO checkout(
            @PathVariable String applicantId,
            @RequestParam(name = "email", required = false) String email) {
        return subscriptionService.createMockPayment(applicantId, email);
    }

    /**
     * Ensures a FREE active subscription for the applicant if none exists,
     * and returns the resulting status.
     */
    @PostMapping("/{applicantId}/default")
    public SubscriptionStatusResponse createDefault(
            @PathVariable String applicantId) {
        return subscriptionService.createDefaultSubscriptionForUser(applicantId);
    }
}
