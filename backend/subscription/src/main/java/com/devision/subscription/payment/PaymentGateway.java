package com.devision.subscription.payment;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;

public interface PaymentGateway {

    PaymentInitiateResponseDTO initiatePayment(
            String applicantId,
            String email,
            String subscriptionId
    );
}
