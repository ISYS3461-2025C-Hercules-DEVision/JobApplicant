package com.devision.subscription.service;

// ===== DTOs returned to controllers / frontend =====
import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;

// ===== Domain enums =====
import com.devision.subscription.enums.PlanType;

// ===== Domain model =====
import com.devision.subscription.model.Subscription;

// ===== Persistence layer =====
import com.devision.subscription.repository.SubscriptionRepository;

// ===== Spring =====
import org.springframework.stereotype.Service;

// ===== Java time utilities =====
import java.time.Instant;
import java.util.Optional;

/**
 * ============================================================
 * SubscriptionServiceImpl
 * ============================================================
 *
 * RESPONSIBILITY:
 * - Central business orchestration for subscription lifecycle.
 * - Decides WHEN to create / deactivate subscriptions.
 * - Delegates all Stripe-specific logic to StripePaymentService.
 *
 * IMPORTANT DESIGN PRINCIPLES:
 * - This service owns the subscription state machine.
 * - Controllers do not contain business logic.
 * - StripePaymentService does not decide business rules.
 *
 * SUBSCRIPTION INVARIANT:
 * - At most ONE active subscription per applicant.
 * - Old subscriptions are deactivated, never overwritten.
 *
 * ============================================================
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

        // Repository for querying and persisting subscriptions
        private final SubscriptionRepository subscriptionRepository;

        // Service responsible for Stripe API interactions
        private final StripePaymentService stripePaymentService;

        /**
         * Constructor-based dependency injection.
         * Keeps dependencies explicit and immutable.
         */
        public SubscriptionServiceImpl(
                        SubscriptionRepository subscriptionRepository,
                        StripePaymentService stripePaymentService) {
                this.subscriptionRepository = subscriptionRepository;
                this.stripePaymentService = stripePaymentService;
        }

        /**
         * ============================================================
         * getMySubscription
         * ============================================================
         *
         * PURPOSE:
         * - Fetch the applicant's current subscription status.
         * - Never returns null.
         *
         * FLOW:
         * 1. Query the latest ACTIVE subscription (deterministic query).
         * 2. If found:
         *    - Map entity → SubscriptionStatusResponse.
         * 3. If not found:
         *    - Return default FREE (inactive) response.
         *
         * DESIGN NOTE:
         * - This method does NOT create subscriptions.
         * - Creation of default FREE plans is handled elsewhere.
         *
         * ============================================================
         */
        @Override
        public SubscriptionStatusResponse getMySubscription(String applicantId) {

                // Query latest active subscription for the applicant
                Optional<Subscription> active =
                                subscriptionRepository.findTopByApplicantIdAndIsActiveTrueOrderByStartDateDesc(
                                                applicantId);

                // Map subscription entity to response DTO
                return active
                                .map(sub -> new SubscriptionStatusResponse(
                                                sub.getPlanType(),    // FREE or PREMIUM
                                                true,                 // active
                                                sub.getExpiryDate()   // may be null
                                ))
                                // If no active subscription exists, return FREE/inactive
                                .orElse(new SubscriptionStatusResponse(
                                                PlanType.FREE,
                                                false,
                                                null
                                ));
        }

        /**
         * ============================================================
         * initiatePayment
         * ============================================================
         *
         * PURPOSE:
         * - Initiate Stripe Checkout for upgrading to PREMIUM.
         * - Returns checkout URL and session information.
         *
         * FLOW:
         * 1. Define fixed subscription price.
         * 2. Delegate to StripePaymentService:
         *    - Creates PaymentTransaction (CREATED).
         *    - Creates Stripe Checkout Session.
         * 3. Build response DTO for frontend redirect.
         *
         * IMPORTANT:
         * - This method does NOT activate PREMIUM.
         * - Actual upgrade happens only after Stripe verification.
         *
         * ============================================================
         */
        @Override
        public PaymentInitiateResponseDTO initiatePayment(
                        String applicantId,
                        String email,
                        String authBearer) {

                // Fixed subscription price (simplified for coursework)
                java.math.BigDecimal amount = java.math.BigDecimal.valueOf(10.00);

                // Stripe currency (ISO code)
                String currency = "USD";

                // Description shown on Stripe Checkout page
                String description = "Premium Subscription";

                // Delegate checkout session creation to StripePaymentService
                com.stripe.model.checkout.Session session =
                                stripePaymentService.initiateCheckout(
                                                applicantId,
                                                email,
                                                amount,
                                                currency,
                                                description
                                );

                // Attempt to retrieve internal transactionId from Stripe metadata
                // Fallback to Stripe session ID if metadata missing
                String transactionId =
                                java.util.Optional.ofNullable(session.getMetadata())
                                                .map(m -> m.get("transactionId"))
                                                .orElse(session.getId());

                // Build response DTO returned to controller / frontend
                return new PaymentInitiateResponseDTO(
                                transactionId,                    // internal payment reference
                                "PENDING",                         // payment not completed yet
                                "Initiated via JA Stripe API",     // status message
                                session.getUrl(),                  // checkout URL
                                session.getUrl(),                  // (duplicate for frontend compatibility)
                                session.getId()                    // Stripe session ID
                );
        }

        /**
         * ============================================================
         * createDefaultSubscriptionForUser
         * ============================================================
         *
         * PURPOSE:
         * - Ensure every applicant has at least one FREE subscription.
         * - Idempotent: safe to call multiple times.
         *
         * FLOW:
         * 1. Check if an active subscription exists.
         * 2. If YES:
         *    - Return existing subscription status.
         * 3. If NO:
         *    - Create new FREE active subscription.
         *    - Persist to DB.
         *
         * ============================================================
         */
        @Override
        public SubscriptionStatusResponse createDefaultSubscriptionForUser(String applicantId) {

                return subscriptionRepository
                                // Check if user already has an active subscription
                                .findByApplicantIdAndIsActiveTrue(applicantId)

                                // If exists, return it
                                .map(sub -> new SubscriptionStatusResponse(
                                                sub.getPlanType(),
                                                true,
                                                sub.getExpiryDate()
                                ))

                                // Otherwise, create a FREE subscription
                                .orElseGet(() -> {
                                        Subscription sub = new Subscription();
                                        sub.setApplicantId(applicantId);   // Owner
                                        sub.setPlanType(PlanType.FREE);     // Default plan
                                        sub.setStartDate(Instant.now());    // Start immediately
                                        sub.setExpiryDate(null);            // FREE has no expiry
                                        sub.setActive(true);                // Active
                                        subscriptionRepository.save(sub);

                                        return new SubscriptionStatusResponse(
                                                        PlanType.FREE,
                                                        true,
                                                        null
                                        );
                                });
        }

        /**
         * ============================================================
         * completePayment
         * ============================================================
         *
         * PURPOSE:
         * - Finalize payment after Stripe redirect.
         * - Upgrade subscription to PREMIUM.
         *
         * FLOW:
         * - Delegates entirely to StripePaymentService:
         *   - Verify Stripe session
         *   - Deactivate old subscriptions
         *   - Create new PREMIUM subscription
         *   - Mark PaymentTransaction SUCCESS
         *
         * ============================================================
         */
        @Override
        public void completePayment(String sessionId) {

                // Business logic handled by StripePaymentService
                stripePaymentService.completePayment(sessionId);
        }

        /**
         * ============================================================
         * cancelSubscription
         * ============================================================
         *
         * PURPOSE:
         * - Downgrade applicant from PREMIUM to FREE.
         *
         * FLOW:
         * 1. Deactivate all active subscriptions.
         * 2. Create a new FREE subscription.
         * 3. Return updated subscription status.
         *
         * DESIGN NOTE:
         * - Uses same deactivate-before-create pattern as upgrade.
         *
         * ============================================================
         */
        @Override
        public SubscriptionStatusResponse cancelSubscription(String applicantId) {

                // Deactivate all active subscriptions
                subscriptionRepository
                                .findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
                                .forEach(s -> {
                                        s.setActive(false);               // Deactivate
                                        subscriptionRepository.save(s);   // Persist change
                                });

                // Create a new FREE active subscription
                Subscription free = new Subscription();
                free.setApplicantId(applicantId);
                free.setPlanType(PlanType.FREE);
                free.setStartDate(Instant.now());
                free.setExpiryDate(null);        // FREE plan never expires
                free.setActive(true);
                subscriptionRepository.save(free);

                // Return updated status
                return new SubscriptionStatusResponse(
                                PlanType.FREE,
                                true,
                                null
                );
        }
}
