package com.devision.subscription.service;

import com.devision.subscription.client.PaymentInitiationClient;
import com.devision.subscription.dto.JmPaymentInitiateRequest;
import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Default implementation of {@link SubscriptionService}.
 *
 * Handles subscription lookups, mock payment flow, and optional forwarding to
 * the external JM Payment API. On successful mock payment, a PREMIUM
 * subscription is created for 30 days and any existing active subscription is
 * deactivated.
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

        private final SubscriptionRepository subscriptionRepository;
        private final PaymentTransactionRepository paymentTransactionRepository;
        private final PaymentInitiationClient paymentInitiationClient;
        private final StripePaymentService stripePaymentService;
        @Value("${payment.forward.enabled:false}")
        private boolean forwardEnabled;
        @Value("${payment.forward.checkout-url:}")
        private String fallbackCheckoutUrl;

        public SubscriptionServiceImpl(
                        SubscriptionRepository subscriptionRepository,
                        PaymentTransactionRepository paymentTransactionRepository,
                        PaymentInitiationClient paymentInitiationClient,
                        StripePaymentService stripePaymentService) {
                this.subscriptionRepository = subscriptionRepository;
                this.paymentTransactionRepository = paymentTransactionRepository;
                this.paymentInitiationClient = paymentInitiationClient;
                this.stripePaymentService = stripePaymentService;
        }

        /**
         * Finds the active subscription for an applicant, returning FREE/inactive
         * when none exists.
         */
        @Override
        public SubscriptionStatusResponse getMySubscription(String applicantId) {
                return subscriptionRepository
                                .findTopByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
                                .map(sub -> new SubscriptionStatusResponse(
                                                sub.getPlanType(),
                                                true,
                                                sub.getExpiryDate()))
                                .orElse(new SubscriptionStatusResponse(
                                                PlanType.FREE,
                                                false,
                                                null));
        }

        /**
         * Either forwards a payment initiation to JM (recording CREATED
         * locally), or simulates a successful payment and provisions a PREMIUM
         * subscription for 30 days.
         */
        @Override
        public PaymentInitiateResponseDTO createMockPayment(String applicantId, String email, String authBearer) {
                // Always use JA-native Stripe now (forwarding disabled in env)
                java.math.BigDecimal amount = java.math.BigDecimal.valueOf(10.00);
                String currency = "USD";
                String description = "Premium Subscription";

                com.stripe.model.checkout.Session session = stripePaymentService.initiateCheckout(
                                applicantId, email, amount, currency, description);

                // Use Stripe session id as transaction id surrogate for response
                String transactionId = java.util.Optional.ofNullable(session.getMetadata())
                                .map(m -> m.get("transactionId"))
                                .orElse(session.getId());

                return new PaymentInitiateResponseDTO(
                                transactionId,
                                "PENDING",
                                "Initiated via JA Stripe API",
                                session.getUrl(),
                                session.getUrl(),
                                session.getId());
        }

        /**
         * Creates a FREE active subscription if the applicant does not already
         * have an active plan, and returns the resulting status.
         */
        @Override
        public SubscriptionStatusResponse createDefaultSubscriptionForUser(String applicantId) {
                // If an active subscription already exists, return it
                return subscriptionRepository
                                .findByApplicantIdAndIsActiveTrue(applicantId)
                                .map(sub -> new SubscriptionStatusResponse(
                                                sub.getPlanType(),
                                                true,
                                                sub.getExpiryDate()))
                                .orElseGet(() -> {
                                        // Create a FREE active subscription
                                        Subscription sub = new Subscription();
                                        sub.setApplicantId(applicantId);
                                        sub.setPlanType(PlanType.FREE);
                                        sub.setStartDate(Instant.now());
                                        sub.setExpiryDate(null); // Free plan: no expiry
                                        sub.setActive(true);

                                        subscriptionRepository.save(sub);

                                        return new SubscriptionStatusResponse(
                                                        PlanType.FREE,
                                                        true,
                                                        null);
                                });
        }

        @Override
        public void completePayment(String sessionId) {
                stripePaymentService.completePayment(sessionId);
        }
}
