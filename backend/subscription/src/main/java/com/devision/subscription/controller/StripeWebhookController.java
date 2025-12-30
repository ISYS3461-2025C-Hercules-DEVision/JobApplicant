package com.devision.subscription.controller;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.service.SubscriptionService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/subscriptions/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PaymentTransactionRepository paymentRepo;
    private final SubscriptionService subscriptionService;

    public StripeWebhookController(
            PaymentTransactionRepository paymentRepo,
            SubscriptionService subscriptionService
    ) {
        this.paymentRepo = paymentRepo;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/stripe")
    public void handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                Session session = (Session) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String applicantId = session.getMetadata().get("applicantId");

                PaymentTransaction tx = new PaymentTransaction();
                tx.setApplicantId(applicantId);
                tx.setEmail(session.getCustomerEmail());
                tx.setPaymentStatus(PaymentStatus.SUCCESS);
                tx.setTransactionTime(Instant.now());
                tx.setStripeSessionId(session.getId());

                paymentRepo.save(tx);

                subscriptionService.activatePremium(applicantId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Stripe webhook failed", e);
        }
    }
}
