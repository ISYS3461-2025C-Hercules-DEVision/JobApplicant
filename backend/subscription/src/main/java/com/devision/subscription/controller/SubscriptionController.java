package com.devision.subscription.controller;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // SAME STYLE AS: GET /api/v1/applicants/{id}
    @GetMapping("/{applicantId}")
    public SubscriptionStatusResponse getMySubscription(
            @PathVariable String applicantId) {
        return subscriptionService.getMySubscription(applicantId);
    }

    // SAME STYLE AS: POST /api/v1/applicants/{id}/avatar
    @PostMapping("/{applicantId}/checkout")
    public PaymentInitiateResponseDTO checkout(
            @PathVariable String applicantId,
            @RequestParam(name = "email", required = false) String email) {
        return subscriptionService.createMockPayment(applicantId, email);
    }

    @PostMapping("/{applicantId}/default")
    public SubscriptionStatusResponse createDefault(
            @PathVariable String applicantId) {
        return subscriptionService.createDefaultSubscriptionForUser(applicantId);
    }
}
