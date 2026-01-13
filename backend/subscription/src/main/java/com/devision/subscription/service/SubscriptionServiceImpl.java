package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

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
        private final StripePaymentService stripePaymentService;

        public SubscriptionServiceImpl(
                        SubscriptionRepository subscriptionRepository,
                        StripePaymentService stripePaymentService) {
                this.subscriptionRepository = subscriptionRepository;
                this.stripePaymentService = stripePaymentService;
        }

        /**
         * Returns the current subscription of an applicant. If none exists, a
         * FREE/inactive default is returned.
         */
        @Override
        public SubscriptionStatusResponse getMySubscription(String applicantId) {
                Optional<Subscription> active = subscriptionRepository
                                .findTopByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId);
                return active
                                .map(sub -> new SubscriptionStatusResponse(
                                                sub.getPlanType(),
                                                true,
                                                sub.getExpiryDate()))
                                .orElse(new SubscriptionStatusResponse(
                                                PlanType.FREE,
                                                false,
                                                null));
        }

        /** Initiates payment via Stripe and returns checkout details. */
        @Override
        public PaymentInitiateResponseDTO initiatePayment(String applicantId, String email, String authBearer) {
                java.math.BigDecimal amount = java.math.BigDecimal.valueOf(10.00);
                String currency = "USD";
                String description = "Premium Subscription";

                com.stripe.model.checkout.Session session = stripePaymentService.initiateCheckout(
                                applicantId, email, amount, currency, description);

                // Use Stripe session id or metadata transactionId
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

        @Override
        public SubscriptionStatusResponse cancelSubscription(String applicantId) {
                // Deactivate all active subscriptions for the user
                subscriptionRepository.findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
                                .forEach(s -> {
                                        s.setActive(false);
                                        subscriptionRepository.save(s);
                                });

                // Create a FREE active subscription (no expiry)
                Subscription free = new Subscription();
                free.setApplicantId(applicantId);
                free.setPlanType(PlanType.FREE);
                free.setStartDate(Instant.now());
                free.setExpiryDate(null);
                free.setActive(true);
                subscriptionRepository.save(free);

                return new SubscriptionStatusResponse(PlanType.FREE, true, null);
        }
}
