package com.devision.subscription.service;

import com.devision.subscription.model.Subscription;

public interface SubscriptionService {

    Subscription activatePremium(String applicantId);

    Subscription getActiveSubscription(String applicantId);
}
