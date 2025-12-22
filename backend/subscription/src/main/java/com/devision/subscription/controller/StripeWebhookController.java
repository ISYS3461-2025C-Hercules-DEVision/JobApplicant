package com.devision.subscription.controller;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.service.SubscriptionService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/subscriptions/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PaymentTransactionRepository paymentRepo;
    private final SubscriptionService subscriptionService;

    @PostMapping
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String applicantId = session.getMetadata().get("applicantId");

                // 1️⃣ Save payment transaction
                PaymentTransaction tx = PaymentTransaction.builder()
                        .applicantId(applicantId)
                        .email(session.getCustomerEmail())
                        .amount(10)
                        .currency("USD")
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionTime(Instant.now())
                        .stripeSessionId(session.getId())
                        .build();

                paymentRepo.save(tx);

                // 2️⃣ Activate subscription
                subscriptionService.activatePremium(
                        applicantId,
                        session.getCustomerEmail()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Stripe webhook error", e);
        }
    }
}
