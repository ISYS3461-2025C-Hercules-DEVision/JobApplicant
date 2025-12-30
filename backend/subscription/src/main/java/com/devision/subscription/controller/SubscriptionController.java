package com.devision.subscription.controller;

import com.devision.subscription.dto.SubscribeRequest;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.service.PaymentClientService;
import com.devision.subscription.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentClientService paymentClientService;

    public SubscriptionController(
            SubscriptionService subscriptionService,
            PaymentClientService paymentClientService
    ) {
        this.subscriptionService = subscriptionService;
        this.paymentClientService = paymentClientService;
    }

    @GetMapping("/me")
    public SubscriptionStatusResponse getMySubscription(
            @RequestHeader(value = "X-Applicant-Id", required = false) String applicantId
    ) {
        if (applicantId == null) {
            return new SubscriptionStatusResponse("FREE", false, null);
        }

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

    @PostMapping("/subscribe")
    public void subscribe(
            @RequestHeader("X-Applicant-Id") String applicantId,
            @RequestBody SubscribeRequest request
    ) {
        var response =
                paymentClientService.initiateApplicantSubscription(
                        applicantId,
                        request.getEmail()
                );

        if ("SUCCESS".equals(response.getStatus())) {
            subscriptionService.activatePremium(
                    applicantId,
                    request.getEmail(),
                    response.getPaymentId(),
                    Instant.now()
            );
        }
    }
}
