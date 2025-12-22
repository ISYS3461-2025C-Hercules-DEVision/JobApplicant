package com.devision.subscription.controller;

import com.devision.subscription.dto.CreateCheckoutSessionRequest;
import com.devision.subscription.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions/checkout")
@RequiredArgsConstructor
public class StripeCheckoutController {

    private final StripeService stripeService;

    @PostMapping
    public String createCheckout(@RequestBody CreateCheckoutSessionRequest request) {
        return stripeService.createCheckoutSession(
                request.getApplicantId(),
                request.getEmail()
        );
    }
}
