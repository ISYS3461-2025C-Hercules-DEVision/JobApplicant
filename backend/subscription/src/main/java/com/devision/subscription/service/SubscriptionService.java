package com.devision.subscription.service;

import com.devision.subscription.model.Subscription;

import java.time.Instant;

public interface SubscriptionService {

    Subscription getActiveSubscription(String applicantId);

    void activatePremium(
            String applicantId,
            String email,
            String paymentId,
            Instant transactionTime
    );
}
