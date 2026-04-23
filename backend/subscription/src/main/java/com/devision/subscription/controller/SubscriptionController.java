package com.devision.subscription.controller;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * SUBSCRIPTION CONTROLLER - API & FLOW DOCUMENTATION
 * ============================================================
 *
 * PURPOSE:
 * - Expose REST endpoints for subscription management.
 * - Handle subscription lookups, Stripe checkout initiation, payment
 * finalization,
 * and subscription cancellation.
 *
 * ROUTES (behind API Gateway under /api/v1/subscriptions):
 * - GET /{applicantId}: Current subscription status for the applicant
 * - POST /{applicantId}/checkout?email=<email>: Initiate Stripe checkout
 * - GET /complete?session_id=<id>: Finalize payment (called by return page)
 * - POST /{applicantId}/cancel: Cancel subscription (set to FREE)
 * - POST /{applicantId}/default: Create FREE subscription if missing
 *
 * PAYMENT FLOW:
 * 1. Frontend user clicks "Subscribe" button.
 * 2. Frontend calls POST /{applicantId}/checkout?email=user@example.com.
 * 3. Controller validates, delegates to subscriptionService.initiatePayment().
 * 4. Service creates PaymentTransaction (CREATED state).
 * 5. Service calls StripePaymentService.initiateCheckout():
 * - Sets customer email on the Stripe session.
 * - Stores Stripe session ID in transaction.
 * - Returns Session with checkoutUrl and sessionId.
 * 6. Controller returns { paymentId, checkoutUrl, sessionId, ... } to frontend.
 * 7. Frontend redirects user to checkoutUrl (Stripe Checkout form).
 * 8. User enters payment details and submits.
 * 9. Stripe charges the card and redirects to success URL:
 * http://localhost:5173/subscription/return?session_id={CHECKOUT_SESSION_ID}
 * 10. Frontend SubscriptionReturnPage component:
 * - Extracts session_id from query params.
 * - Calls subscriptionService.completePayment(sessionId).
 * - This hits GET /complete?sessionId=... (or ?session_id=...).
 * 11. Controller receives sessionId, calls
 * subscriptionService.completePayment().
 * 12. Service:
 * - Retrieves Stripe session details (verify payment success).
 * - Deactivates all existing active subscriptions for the applicant.
 * - Creates new PREMIUM subscription (30 days from now).
 * - Updates PaymentTransaction to SUCCESS.
 * 13. Frontend navigates to /subscription; user sees PREMIUM status.
 *
 * CANCELLATION FLOW:
 * 1. Premium user clicks "Cancel Subscription" button.
 * 2. Frontend prompts confirmation dialog.
 * 3. Frontend calls POST /{applicantId}/cancel.
 * 4. Service:
 * - Deactivates all active subscriptions.
 * - Creates new FREE subscription (no expiry).
 * 5. Frontend emits 'subscription-updated' event.
 * 6. ProfileHeader and SubscriptionPage listen, refetch status.
 *
 * ERROR HANDLING:
 * - Missing STRIPE_API_KEY env: 500 "Stripe API key not configured".
 * - Invalid session_id: 500 with Stripe exception details.
 * - Missing applicantId: 400 "Invalid applicant ID".
 * - Missing session_id on complete: 400 "Missing session id".
 *
 * NOTES:
 * - Email is optional on checkout; if not provided, Stripe won't pre-fill it.
 * - Complete endpoint accepts both ?sessionId= and ?session_id= for robustness.
 * - All endpoints are served behind API Gateway (no auth check here; gateway
 * enforces).
 * ============================================================
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
            PaymentInitiateResponseDTO dto = subscriptionService.initiatePayment(applicantId, email, authorization);
            return org.springframework.http.ResponseEntity.ok(dto);
        } catch (IllegalStateException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of(
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Map.of(
                    "error", "Failed to initiate payment",
                    "details", ex.getMessage()));
        }
    }

    /**
     * Completes a payment after Stripe redirects the frontend with a session_id
     * query parameter. The frontend should call this endpoint with that session
     * id to finalize the subscription.
     */
    @GetMapping("/complete")
    public String complete(
            @RequestParam(name = "sessionId", required = false) String sessionId,
            @RequestParam(name = "session_id", required = false) String session_id) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = session_id;
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Missing session id");
        }
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
