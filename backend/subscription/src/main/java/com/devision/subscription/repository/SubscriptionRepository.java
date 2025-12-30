package com.devision.subscription.repository;

import com.devision.subscription.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * SubscriptionRepository
 *
 * PURPOSE:
 * - Provides database access for Subscription entities
 *
 * DESIGN NOTES:
 * - Only ONE active subscription is allowed per applicant
 * - Queries are simple and synchronous
 *
 * SRS MAPPING:
 * - Used to determine whether an applicant is PREMIUM (5.1.1)
 */
public interface SubscriptionRepository
        extends MongoRepository<Subscription, String> {

    /**
     * Find active subscription for an applicant
     */
    Optional<Subscription> findByApplicantIdAndIsActiveTrue(String applicantId);
}
