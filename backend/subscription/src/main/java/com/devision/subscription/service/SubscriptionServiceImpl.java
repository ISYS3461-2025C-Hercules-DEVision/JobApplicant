package com.devision.subscription.service;

import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private static final int PREMIUM_DURATION_DAYS = 30;

    @Override
    public Subscription activatePremium(String applicantId, String email) {

        Instant now = Instant.now();

        Subscription subscription = Subscription.builder()
                .applicantId(applicantId)
                .email(email)
                .planType(PlanType.PREMIUM)
                .active(true)
                .startAt(now)
                .endAt(now.plus(PREMIUM_DURATION_DAYS, ChronoUnit.DAYS))
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getActiveSubscription(String applicantId) {
        Optional<Subscription> subOpt =
                subscriptionRepository.findByApplicantIdAndActiveTrue(applicantId);

        if (subOpt.isEmpty()) {
            return null;
        }

        Subscription sub = subOpt.get();
        expireSubscriptionIfNeeded(sub);
        return sub.isActive() ? sub : null;
    }

    @Override
    public boolean isPremium(String applicantId) {
        return getActiveSubscription(applicantId) != null;
    }

    @Override
    public void expireSubscriptionIfNeeded(Subscription subscription) {
        if (subscription.isActive()
                && subscription.getEndAt().isBefore(Instant.now())) {

            subscription.setActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}
