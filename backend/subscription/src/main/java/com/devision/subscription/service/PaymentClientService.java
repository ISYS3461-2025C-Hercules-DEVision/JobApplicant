package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;

public interface PaymentClientService {

    PaymentInitiateResponseDTO initiateApplicantSubscription(
            String applicantId,
            String email
    );
}
