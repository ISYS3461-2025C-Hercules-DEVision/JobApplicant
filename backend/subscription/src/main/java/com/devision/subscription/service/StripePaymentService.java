package com.devision.subscription.service;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class StripePaymentService {

    private static final Logger log = LoggerFactory.getLogger(StripePaymentService.class);

    private final PaymentTransactionRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public StripePaymentService(PaymentTransactionRepository paymentRepository,
                                SubscriptionRepository subscriptionRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void init() {
        if (stripeApiKey != null && !stripeApiKey.isBlank()) {
            Stripe.apiKey = stripeApiKey;
        } else {
            log.warn("Stripe API key is not configured; initiation will fail until set");
        }
    }

    public Session initiateCheckout(String applicantId, String email,
                                    BigDecimal amount, String currency, String description) {
        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            throw new IllegalStateException("Stripe API key not configured");
        }
        try {
            // Create transaction in PENDING/CREATED state
            PaymentTransaction tx = new PaymentTransaction();
            tx.setApplicantId(applicantId);
            tx.setEmail(email);
            tx.setPaymentStatus(PaymentStatus.CREATED);
            tx.setTransactionTime(Instant.now());
            PaymentTransaction saved = paymentRepository.save(tx);

            // Create Stripe checkout session
                SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency.toLowerCase())
                                                    .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(description != null ? description : "Premium Subscription")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("transactionId", saved.getId())
                    .putMetadata("applicantId", applicantId)
                    ;

                if (email != null && !email.isBlank()) {
                builder.setCustomerEmail(email);
                }

                SessionCreateParams params = builder.build();

            Session session = Session.create(params);

            // Update tx with Stripe session id
            saved.setStripeSessionId(session.getId());
            paymentRepository.save(saved);

            return session;

        } catch (StripeException e) {
            log.error("Stripe error creating checkout session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment session: " + e.getMessage());
        }
    }

    public void completePayment(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            PaymentTransaction tx = paymentRepository.findByStripeSessionId(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found for session: " + sessionId));

            if (tx.getPaymentStatus() == PaymentStatus.SUCCESS) {
                log.info("Payment already completed for session: {}", sessionId);
                return;
            }

            // Mark transaction success
            tx.setPaymentStatus(PaymentStatus.SUCCESS);
            tx.setTransactionTime(Instant.now());
            paymentRepository.save(tx);

            // Deactivate all existing active subscriptions (if any)
            subscriptionRepository.findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(tx.getApplicantId())
                    .forEach(old -> {
                        old.setActive(false);
                        subscriptionRepository.save(old);
                    });

            // Create PREMIUM for 30 days
            Subscription sub = new Subscription();
            sub.setApplicantId(tx.getApplicantId());
            sub.setPlanType(com.devision.subscription.enums.PlanType.PREMIUM);
            sub.setStartDate(Instant.now());
            sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
            sub.setActive(true);
            subscriptionRepository.save(sub);

        } catch (StripeException e) {
            log.error("Stripe error retrieving session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete payment: " + e.getMessage());
        }
    }
}
