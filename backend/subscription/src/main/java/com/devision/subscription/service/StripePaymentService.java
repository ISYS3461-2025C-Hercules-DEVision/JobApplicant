package com.devision.subscription.service;

import com.devision.subscription.enums.PlanType;
import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles Stripe Checkout session creation and completion.
 */
@Service
public class StripePaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public StripePaymentService(PaymentTransactionRepository paymentTransactionRepository,
                                SubscriptionRepository subscriptionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Creates a Stripe Checkout session and records a CREATED transaction.
     */
    public Session initiateCheckout(String applicantId, String email,
                                    BigDecimal amount, String currency, String description) {
        Stripe.apiKey = stripeApiKey;

        long unitAmount = amount.multiply(BigDecimal.valueOf(100)).longValue();

        // Create transaction record first
        PaymentTransaction tx = new PaymentTransaction();
        tx.setApplicantId(applicantId);
        tx.setEmail(email);
        tx.setPaymentStatus(PaymentStatus.CREATED);
        tx.setTransactionTime(Instant.now());
        paymentTransactionRepository.save(tx);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("transactionId", tx.getId());
        metadata.put("applicantId", applicantId);

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(currency.toLowerCase())
                .setUnitAmount(unitAmount)
                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(description)
                        .build())
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putAllMetadata(metadata)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .build();

        try {
            Session session = Session.create(params);
            tx.setStripeSessionId(session.getId());
            paymentTransactionRepository.save(tx);
            return session;
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }

    /**
     * Completes payment and activates PREMIUM subscription for 30 days.
     */
    public void completePayment(String sessionId) {
        Stripe.apiKey = stripeApiKey;
        try {
            Session session = Session.retrieve(sessionId);

                // Find transaction and applicant
                PaymentTransaction tx = paymentTransactionRepository
                    .findByStripeSessionId(sessionId)
                    .orElse(null);

            String applicantId = tx != null ? tx.getApplicantId() : session.getMetadata().get("applicantId");

            // Deactivate existing active subscriptions
            subscriptionRepository.findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
                    .forEach(s -> {
                        s.setActive(false);
                        subscriptionRepository.save(s);
                    });

            // Activate PREMIUM
            Subscription premium = new Subscription();
            premium.setApplicantId(applicantId);
            premium.setPlanType(PlanType.PREMIUM);
            premium.setStartDate(Instant.now());
            premium.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
            premium.setActive(true);
            subscriptionRepository.save(premium);

            if (tx != null) {
                tx.setPaymentStatus(PaymentStatus.SUCCESS);
                tx.setTransactionTime(Instant.now());
                paymentTransactionRepository.save(tx);
            }
        } catch (StripeException e) {
            throw new RuntimeException("Failed to complete Stripe session", e);
        }
    }
}
