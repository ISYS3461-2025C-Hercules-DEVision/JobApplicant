package com.devision.subscription.service;

import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;

    public SubscriptionServiceImpl(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Subscription activatePremium(String applicantId) {

        Instant now = Instant.now();

        Subscription sub = new Subscription();
        sub.setApplicantId(applicantId);
        sub.setPlanType(PlanType.PREMIUM);
        sub.setStartDate(now);
        sub.setExpiryDate(now.plus(30, ChronoUnit.DAYS));
        sub.setActive(true);

        return repository.save(sub);
    }

    @Override
    public Subscription getActiveSubscription(String applicantId) {

        Optional<Subscription> subOpt =
                repository.findByApplicantIdAndIsActiveTrue(applicantId);

        if (subOpt.isEmpty()) return null;

        Subscription sub = subOpt.get();

        if (sub.getExpiryDate().isBefore(Instant.now())) {
            sub.setActive(false);
            repository.save(sub);
            return null;
        }

        return sub;
    }
}
