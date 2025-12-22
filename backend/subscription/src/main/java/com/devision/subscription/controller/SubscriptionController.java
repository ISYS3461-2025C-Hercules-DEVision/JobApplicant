package com.devision.subscription.controller;

import com.devision.subscription.model.Subscription;
import com.devision.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/status/{applicantId}")
    public Subscription getSubscriptionStatus(@PathVariable String applicantId) {
        return subscriptionService.getActiveSubscription(applicantId);
    }
}
