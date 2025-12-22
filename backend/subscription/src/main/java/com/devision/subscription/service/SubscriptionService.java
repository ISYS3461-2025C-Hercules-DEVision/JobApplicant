package com.devision.subscription.service;

import com.devision.subscription.model.Subscription;

public interface SubscriptionService {

    Subscription activatePremium(String applicantId, String email);

    Subscription getActiveSubscription(String applicantId);

    boolean isPremium(String applicantId);

    void expireSubscriptionIfNeeded(Subscription subscription);
}
