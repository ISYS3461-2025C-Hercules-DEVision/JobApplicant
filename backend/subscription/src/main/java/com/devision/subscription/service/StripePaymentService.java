package com.devision.subscription.service;

// ====== Domain enums ======
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.enums.PaymentStatus;

// ====== Domain models ======
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;

// ====== Persistence layer ======
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;

// ====== Stripe SDK ======
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

// ====== Spring ======
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// ====== Java utilities ======
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * StripePaymentService
 * ============================================================
 *
 * RESPONSIBILITY:
 * - Encapsulates ALL Stripe-related logic.
 * - Creates Stripe Checkout sessions.
 * - Verifies completed payments.
 * - Activates PREMIUM subscriptions upon successful payment.
 *
 * DESIGN NOTES:
 * - This class does NOT expose HTTP endpoints.
 * - It is called only by SubscriptionServiceImpl.
 * - Business orchestration (state machine) remains outside.
 *
 * SECURITY:
 * - Stripe verification is always server-side.
 * - Frontend never activates subscriptions.
 *
 * PERSISTENCE:
 * - PaymentTransaction is used as an immutable audit log.
 * - Subscription records follow deactivate-before-create rule.
 *
 * ============================================================
 */
@Service
public class StripePaymentService {

    // Repository for persisting payment transactions (audit log)
    private final PaymentTransactionRepository paymentTransactionRepository;

    // Repository for managing subscription state
    private final SubscriptionRepository subscriptionRepository;

    // Stripe secret API key (sk_test_... or sk_live_...)
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    // Frontend redirect URL after successful payment
    @Value("${stripe.success-url}")
    private String successUrl;

    // Frontend redirect URL if user cancels payment
    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    /**
     * Constructor-based dependency injection.
     * Ensures repositories are immutable and available at runtime.
     */
    public StripePaymentService(
            PaymentTransactionRepository paymentTransactionRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * ============================================================
     * initiateCheckout
     * ============================================================
     *
     * PURPOSE:
     * - Creates a Stripe Checkout session.
     * - Records a PaymentTransaction with status CREATED.
     * - Returns Stripe-hosted checkout URL to frontend.
     *
     * INPUT:
     * - applicantId : ID of applicant subscribing
     * - email       : Optional email (prefill Stripe form)
     * - amount      : Subscription price (e.g., 30.00)
     * - currency    : ISO currency (e.g., "USD")
     * - description : Product name shown on Stripe
     *
     * OUTPUT:
     * - Stripe Session object containing:
     *   - sessionId
     *   - checkoutUrl
     *
     * SIDE EFFECTS:
     * - Saves PaymentTransaction with status CREATED
     *
     * ============================================================
     */
    public Session initiateCheckout(
            String applicantId,
            String email,
            BigDecimal amount,
            String currency,
            String description
    ) {

        // Safety check: Stripe API key must be configured
        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            throw new IllegalStateException("Stripe API key not configured");
        }

        // Configure Stripe SDK with secret key
        Stripe.apiKey = stripeApiKey;

        // Convert amount to smallest currency unit (Stripe requires cents)
        long unitAmount = amount.multiply(BigDecimal.valueOf(100)).longValue();

        // ===============================
        // STEP 1: Create payment record
        // ===============================

        PaymentTransaction tx = new PaymentTransaction();
        tx.setApplicantId(applicantId);               // Who is paying
        tx.setEmail(email);                           // Optional email
        tx.setPaymentStatus(PaymentStatus.CREATED);  // Initial state
        tx.setTransactionTime(Instant.now());         // Timestamp
        paymentTransactionRepository.save(tx);        // Persist to DB

        // ===============================
        // STEP 2: Prepare Stripe metadata
        // ===============================

        // Metadata is echoed back by Stripe in callbacks
        Map<String, String> metadata = new HashMap<>();
        metadata.put("transactionId", tx.getId());    // Internal reference
        metadata.put("applicantId", applicantId);     // Business reference

        // ===============================
        // STEP 3: Build price information
        // ===============================

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency.toLowerCase()) // Stripe expects lowercase
                        .setUnitAmount(unitAmount)           // Amount in cents
                        .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(description) // Product name
                                        .build()
                        )
                        .build();

        // ===============================
        // STEP 4: Build Checkout session
        // ===============================

        SessionCreateParams.Builder builder =
                SessionCreateParams.builder()
                        // Single line item (monthly subscription fee)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(priceData)
                                        .build()
                        )
                        // One-time payment (not Stripe recurring subscription)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        // Allow only card payments
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        // Attach metadata for later verification
                        .putAllMetadata(metadata)
                        // Stripe replaces placeholder with real session ID
                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        // Redirect if user cancels payment
                        .setCancelUrl(cancelUrl);

        // Optional: Prefill customer email in Stripe UI
        if (email != null && !email.isBlank()) {
            builder.setCustomerEmail(email);
        }

        SessionCreateParams params = builder.build();

        // ===============================
        // STEP 5: Call Stripe API
        // ===============================

        try {
            // Create Stripe Checkout session
            Session session = Session.create(params);

            // Persist Stripe session ID for later verification
            tx.setStripeSessionId(session.getId());
            paymentTransactionRepository.save(tx);

            // Return session (URL + ID) to caller
            return session;

        } catch (StripeException e) {
            // Wrap Stripe exception to avoid leaking SDK details
            throw new RuntimeException("Stripe error: " + e.getMessage(), e);
        }
    }

    /**
     * ============================================================
     * completePayment
     * ============================================================
     *
     * PURPOSE:
     * - Verifies Stripe payment server-side.
     * - Deactivates existing subscriptions.
     * - Creates a new PREMIUM subscription (30 days).
     * - Marks PaymentTransaction as SUCCESS.
     *
     * INPUT:
     * - sessionId : Stripe Checkout session ID
     *
     * OUTPUT:
     * - void (state changes only)
     *
     * SIDE EFFECTS:
     * - Updates Subscription collection
     * - Updates PaymentTransaction status
     *
     * ============================================================
     */
    public void completePayment(String sessionId) {

        // Configure Stripe SDK
        Stripe.apiKey = stripeApiKey;

        try {
            // ===============================
            // STEP 1: Retrieve Stripe session
            // ===============================

            Session session = Session.retrieve(sessionId);

            // ===============================
            // STEP 2: Locate transaction
            // ===============================

            PaymentTransaction tx = paymentTransactionRepository
                    .findByStripeSessionId(sessionId)
                    .orElse(null);

            // Determine applicant ID:
            // - Prefer DB record
            // - Fallback to Stripe metadata
            String applicantId =
                    tx != null
                            ? tx.getApplicantId()
                            : session.getMetadata().get("applicantId");

            // ===============================
            // STEP 3: Deactivate existing subs
            // ===============================

            subscriptionRepository
                    .findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
                    .forEach(s -> {
                        s.setActive(false);            // Deactivate
                        subscriptionRepository.save(s);
                    });

            // ===============================
            // STEP 4: Create PREMIUM sub
            // ===============================

            Subscription premium = new Subscription();
            premium.setApplicantId(applicantId);                   // Owner
            premium.setPlanType(PlanType.PREMIUM);                 // Upgrade
            premium.setStartDate(Instant.now());                   // Now
            premium.setExpiryDate(
                    Instant.now().plus(30, ChronoUnit.DAYS)       // +30 days
            );
            premium.setActive(true);                                // Active
            subscriptionRepository.save(premium);

            // ===============================
            // STEP 5: Mark transaction success
            // ===============================

            if (tx != null) {
                tx.setPaymentStatus(PaymentStatus.SUCCESS);
                tx.setTransactionTime(Instant.now());
                paymentTransactionRepository.save(tx);
            }

        } catch (StripeException e) {
            // Stripe verification failed or session invalid
            throw new RuntimeException("Failed to complete Stripe session", e);
        }
    }
}
