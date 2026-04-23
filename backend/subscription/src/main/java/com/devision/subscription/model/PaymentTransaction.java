package com.devision.subscription.model;

import com.devision.subscription.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * ============================================================
 * PAYMENT TRANSACTION MODEL - PAYMENT AUDIT LOG
 * ============================================================
 *
 * PURPOSE:
 * - Audit log of payment attempts (Stripe Checkout sessions).
 * - Tracks state transitions: CREATED -> SUCCESS or FAILED.
 * - Links Stripe session ID to applicant and subscription activation.
 * - Enables payment history queries and idempotency (if same sessionId, use
 * existing tx).
 *
 * DATABASE COLLECTION: payment_transactions
 *
 * SCHEMA (MongoDB):
 * {
 * "_id": ObjectId, // Auto-generated transaction ID
 * "applicantId": "user-id-123", // Subscriber ID
 * "email": "user@example.com", // Email for Stripe receipt
 * "paymentStatus": "SUCCESS", // Enum: CREATED | SUCCESS | FAILED
 * "transactionTime": ISODate(...), // Last status update timestamp
 * "stripeSessionId": "cs_test_a1mX74Jx..." // Stripe checkout session ID
 * }
 *
 * LIFECYCLE:
 * 
 * 1. CREATED (initiateCheckout):
 * - PaymentTransaction created with status=CREATED.
 * - applicantId, email, transactionTime=now set.
 * - stripeSessionId = null (filled after Stripe.create()).
 * - After Stripe session created: stripeSessionId updated and saved.
 * 
 * 2. SUCCESS (completePayment):
 * - Stripe session retrieved and verified (payment completed).
 * - Transaction status -> SUCCESS.
 * - transactionTime updated to completion time.
 * - PREMIUM subscription created for 30 days.
 * 
 * 3. FAILED (optional; not currently implemented):
 * - If Stripe session expires or user cancels, status could be FAILED.
 * - Not auto-set; would require webhook or retry logic.
 *
 * USAGE PATTERNS:
 * 
 * 1. Create transaction on checkout initiation:
 * PaymentTransaction tx = new PaymentTransaction();
 * tx.setApplicantId(applicantId);
 * tx.setEmail(email);
 * tx.setPaymentStatus(PaymentStatus.CREATED);
 * tx.setTransactionTime(Instant.now());
 * PaymentTransaction saved = repo.save(tx); // gets _id
 * // Later: update with stripeSessionId
 * saved.setStripeSessionId(session.getId());
 * repo.save(saved);
 * 
 * 2. Find transaction by Stripe session ID:
 * repo.findByStripeSessionId(sessionId)
 * Returns: PaymentTransaction with metadata to identify applicant, email.
 * 
 * 3. Query all transactions for an applicant (optional):
 * repo.findByApplicantId(applicantId) // not implemented; add if needed for
 * history
 * Returns: All payment attempts (for UI: invoice/receipt generation).
 *
 * FIELDS:
 * - id: MongoDB _id; auto-generated.
 * - applicantId: Foreign key to applicant.
 * - email: Applicant email (for Stripe receipt and correlation).
 * - paymentStatus: Enum {CREATED, SUCCESS, FAILED}.
 * - transactionTime: When status last changed (millisecond precision).
 * - stripeSessionId: Stripe Checkout session ID (unique constraint not
 * enforced; ideally should be).
 *
 * DESIGN NOTES:
 * - No amount field: Price is fixed at $10.00 (set in SubscriptionServiceImpl).
 * - No currency field: Fixed to USD.
 * - No metadata: Stripe metadata stored in session, not here (redundant).
 * - No idempotency key: Calling completePayment(sessionId) twice may create
 * duplicate subscriptions.
 * (Frontend prevents this; backend could add request deduplication in future.)
 * - stripeSessionId should be unique but not enforced; queries use it to find
 * record.
 *
 * INTEGRATION:
 * - Stripe -> PaymentTransaction: Session.create() -> saved with CREATED.
 * - PaymentTransaction -> Subscription: On SUCCESS, new PREMIUM subscription
 * created.
 * - Audit: Payment history available for billing/troubleshooting (not currently
 * used in UI).
 * ============================================================
 */
@Document(collection = "payment_transactions")
public class PaymentTransaction {

    @Id
    private String id;

    private String applicantId;
    private String email;
    private PaymentStatus paymentStatus;
    private Instant transactionTime;
    private String stripeSessionId;

    // ===== getters & setters =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Instant transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }
}
