package com.devision.subscription.model;

import com.devision.subscription.enums.PlanType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * ============================================================
 * SUBSCRIPTION MODEL - PERSISTENCE ENTITY
 * ============================================================
 *
 * PURPOSE:
 * - Represents a subscription record in MongoDB.
 * - Tracks plan type (FREE or PREMIUM), active state, and expiry.
 *
 * INVARIANTS:
 * - One active subscription per applicant (enforced by service logic).
 * - FREE plans have no expiry (expiryDate = null).
 * - PREMIUM plans have a 30-day expiry from creation.
 * - Active subscription has latest startDate (enforced by queries).
 *
 * DATABASE COLLECTION: subscriptions
 *
 * SCHEMA (MongoDB):
 * {
 * "_id": ObjectId, // Auto-generated subscriptionId
 * "applicantId": "user-id-123", // Foreign key to applicant
 * "planType": "PREMIUM", // Enum: FREE | PREMIUM
 * "startDate": ISODate(...), // When subscription activated
 * "expiryDate": ISODate(...), // When subscription expires (null for FREE)
 * "isActive": true, // Boolean flag (indexed for queries)
 * }
 *
 * USAGE PATTERNS:
 * 
 * 1. Get current active subscription:
 * repo.findTopByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
 * Returns: Latest active subscription or empty.
 * 
 * 2. Get all active subscriptions (for deactivation):
 * repo.findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId)
 * Returns: All active subs (usually 0 or 1); used to deactivate before creating
 * new.
 * 
 * 3. Create new PREMIUM after payment:
 * Subscription sub = new Subscription();
 * sub.setApplicantId(applicantId);
 * sub.setPlanType(PlanType.PREMIUM);
 * sub.setStartDate(Instant.now());
 * sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
 * sub.setActive(true);
 * repo.save(sub);
 * 
 * 4. Downgrade to FREE:
 * Deactivate all active: sub.setActive(false); repo.save(sub);
 * Create FREE: sub.setPlanType(PlanType.FREE); sub.setExpiryDate(null);
 * repo.save(sub);
 *
 * FIELDS:
 * - subscriptionId: MongoDB _id; auto-generated; immutable once saved.
 * - applicantId: Links to applicant profile (no FK constraint in MongoDB;
 * validated in service).
 * - planType: Enum (FREE | PREMIUM); determines feature access.
 * - startDate: When subscription was activated (Instant; precise to
 * milliseconds).
 * - expiryDate: When subscription expires; null = no expiry (FREE plans only).
 * - isActive: Boolean flag; true = currently active, false = historical record.
 *
 * IMPORTANT NOTES:
 * - isActive is a flag; service logic enforces uniqueness (not DB constraint).
 * - Queries order by startDate DESC to get latest; allows historical tracking.
 * - Frontend UI reads only active subs; backend returns latest active or
 * default FREE.
 * - No payment reference stored here; linked via
 * PaymentTransaction.applicantId.
 * ============================================================
 */
@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String subscriptionId;
    private String applicantId;
    private PlanType planType;
    private Instant startDate;
    private Instant expiryDate;
    private boolean isActive;

    public Subscription() {
    }

    public Subscription(String applicantId, PlanType planType,
            Instant startDate, Instant expiryDate, boolean isActive) {
        this.applicantId = applicantId;
        this.planType = planType;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
