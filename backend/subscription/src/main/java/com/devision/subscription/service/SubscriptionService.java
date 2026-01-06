package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.model.Subscription;

public interface SubscriptionService {

    Subscription getActiveSubscription(String applicantId);

    PaymentInitiateResponseDTO startSubscription(String applicantId, String email);

    void markSubscriptionPaid(String applicantId);
}


