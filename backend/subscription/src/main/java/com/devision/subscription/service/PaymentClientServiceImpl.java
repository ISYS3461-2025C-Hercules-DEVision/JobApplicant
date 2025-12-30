package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateRequestDTO;
import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Profile("!dev")
public class PaymentClientServiceImpl implements PaymentClientService {

    private final WebClient webClient;

    public PaymentClientServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public PaymentInitiateResponseDTO initiateApplicantSubscription(
            String applicantId,
            String email
    ) {
        PaymentInitiateRequestDTO request =
                new PaymentInitiateRequestDTO(
                        applicantId,
                        "APPLICANT",
                        email,
                        1000
                );

        return webClient.post()
                .uri("http://payment/api/payments/initiate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentInitiateResponseDTO.class)
                .block();
    }
}
