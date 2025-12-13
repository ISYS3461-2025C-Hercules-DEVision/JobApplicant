package com.devision.subscription.repository;

import com.devision.subscription.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByApplicantId(String applicantId);

    Optional<Subscription> findByApplicantIdAndIsActiveTrue(String applicantId);

    List<Subscription> findByIsActiveTrueAndExpiryDateBefore(Instant now);
}
