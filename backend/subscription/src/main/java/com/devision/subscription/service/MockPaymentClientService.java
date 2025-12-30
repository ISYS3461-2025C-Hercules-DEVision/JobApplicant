package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("dev")
public class MockPaymentClientService implements PaymentClientService {

    @Override
    public PaymentInitiateResponseDTO initiateApplicantSubscription(
            String applicantId,
            String email
    ) {
        return new PaymentInitiateResponseDTO(
                "MOCK-" + UUID.randomUUID(),
                "SUCCESS",
                "Mock payment success"
        );
    }
}
