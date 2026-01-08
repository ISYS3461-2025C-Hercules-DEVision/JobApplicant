package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;

public interface SubscriptionService {

    SubscriptionStatusResponse getMySubscription(String applicantId);

    PaymentInitiateResponseDTO createMockPayment(String applicantId, String email);

    SubscriptionStatusResponse createDefaultSubscriptionForUser(String applicantId);
}
