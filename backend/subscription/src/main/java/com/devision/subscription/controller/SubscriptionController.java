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

    @GetMapping("/{applicantId}")
    public SubscriptionStatusResponse getMySubscription(
            @PathVariable String applicantId) {
        return subscriptionService.getMySubscription(applicantId);
    }
    
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
