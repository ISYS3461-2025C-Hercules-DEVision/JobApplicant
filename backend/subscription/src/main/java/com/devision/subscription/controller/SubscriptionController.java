package com.devision.subscription.controller;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public SubscriptionStatusResponse mySubscription(
            @AuthenticationPrincipal Jwt jwt) {

        String applicantId = jwt.getClaim("applicantId");

        Subscription sub = subscriptionService.getActiveSubscription(applicantId);

        if (sub == null) {
            return new SubscriptionStatusResponse("FREE", false, null);
        }

        return new SubscriptionStatusResponse(
                sub.getPlanType().name(),
                sub.isActive(),
                sub.getExpiryDate()
        );
    }

    @PostMapping("/checkout")
    public PaymentInitiateResponseDTO checkout(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String applicantId = jwt.getClaim("applicantId");
        String email = jwt.getClaim("email");

        return subscriptionService.startSubscription(applicantId, email);
    }

    @PostMapping("/mock/confirm")
    public void confirmMockPayment(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String applicantId = jwt.getClaim("applicantId");
        subscriptionService.markSubscriptionPaid(applicantId);
    }

}
