package com.devision.subscription.controller;

import com.devision.subscription.dto.CheckoutResponse;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.service.StripeService;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final StripeService stripeService;

    public SubscriptionController(
            SubscriptionService subscriptionService,
            StripeService stripeService
    ) {
        this.subscriptionService = subscriptionService;
        this.stripeService = stripeService;
    }

    /**
     * Get current subscription status.
     * In dev mode, X-Applicant-Id is optional to avoid noise
     * from browser / health checks.
     */
    @GetMapping("/me")
    public SubscriptionStatusResponse getMySubscription(
            @RequestHeader(value = "X-Applicant-Id", required = false) String applicantId
    ) {
        // Dev-friendly behavior
        if (applicantId == null || applicantId.isBlank()) {
            return new SubscriptionStatusResponse("FREE", false, null);
        }

        Subscription sub =
                subscriptionService.getActiveSubscription(applicantId);

        if (sub == null) {
            return new SubscriptionStatusResponse("FREE", false, null);
        }

        return new SubscriptionStatusResponse(
                sub.getPlanType().name(),
                sub.isActive(),
                sub.getExpiryDate()
        );
    }

    /**
     * Create Stripe checkout session.
     * This MUST have applicant identity.
     */
    @PostMapping("/checkout")
    public CheckoutResponse checkout(
            @RequestHeader(value = "X-Applicant-Id", required = false) String applicantId,
            @RequestHeader(value = "X-Applicant-Email", required = false) String email
    ) {
        if (applicantId == null || email == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "X-Applicant-Id and X-Applicant-Email headers are required"
            );
        }

        return new CheckoutResponse(
                stripeService.createCheckoutSession(applicantId, email)
        );
    }
}
