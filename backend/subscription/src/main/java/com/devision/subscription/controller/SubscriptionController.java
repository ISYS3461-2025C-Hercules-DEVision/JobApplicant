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
 * - POST /{applicantId}/checkout: Initiate Stripe checkout for subscription
 * - POST /{applicantId}/cancel: Cancel current subscription (set to FREE)
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
     * Initiates a subscription payment for the applicant via Stripe Checkout.
     */
    @PostMapping("/{applicantId}/checkout")
    public org.springframework.http.ResponseEntity<?> checkout(
            @PathVariable String applicantId,
            @RequestParam(name = "email", required = false) String email,
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        try {
            PaymentInitiateResponseDTO dto = subscriptionService.createMockPayment(applicantId, email, authorization);
            return org.springframework.http.ResponseEntity.ok(dto);
        } catch (IllegalStateException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of(
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Map.of(
                    "error", "Failed to initiate payment"));
        }
    }

    /**
     * Completes a payment after Stripe redirects the frontend with a session_id
     * query parameter. The frontend should call this endpoint with that session
     * id to finalize the subscription.
     */
    @GetMapping("/complete")
    public String complete(@RequestParam("sessionId") String sessionId) {
        subscriptionService.completePayment(sessionId);
        return "Payment completed";
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

    /**
     * Cancels the current subscription (if any) and sets plan to FREE (active).
     */
    @PostMapping("/{applicantId}/cancel")
    public SubscriptionStatusResponse cancel(
            @PathVariable String applicantId) {
        return subscriptionService.cancelSubscription(applicantId);
    }
}
