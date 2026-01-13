package com.devision.subscription.client;

import com.devision.subscription.dto.JmPaymentInitiateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Minimal WebClient wrapper for forwarding subscription payment initiation
 * requests to the JM Payment API. Activated when forwarding is enabled.
 */
@Component
@Profile("forwarding")
public class PaymentInitiationClient {

    private final WebClient webClient;
    private final String initiatePath;
    private final String bearer;

    public PaymentInitiationClient(
            @Value("${payment.forward.base-url}") String baseUrl,
            @Value("${payment.forward.initiate-path}") String initiatePath,
            @Value("${payment.forward.bearer}") String bearer) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.initiatePath = initiatePath;
        this.bearer = bearer;
    }

    /**
     * Calls JM's payment initiation endpoint and returns the raw response map
     * (containing at least transactionId and status when successful).
     */
    public Mono<Map<String, Object>> initiate(JmPaymentInitiateRequest request) {
        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(initiatePath)
                .bodyValue(request);

        if (bearer != null && !bearer.isBlank()) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
        }

        return spec.retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }
}
