package com.devision.subscription.repository;

import com.devision.subscription.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for accessing applicant subscriptions.
 */
public interface SubscriptionRepository
        extends MongoRepository<Subscription, String> {
    Optional<Subscription> findByApplicantIdAndIsActiveTrue(String applicantId);

    // Prefer deterministic single-result queries to avoid IncorrectResultSize
    Optional<Subscription> findTopByApplicantIdAndIsActiveTrueOrderByStartDateDesc(String applicantId);

    java.util.List<Subscription> findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(String applicantId);
}
