package com.devision.subscription.service;

public interface StripeService {
    String createCheckoutSession(String applicantId, String email);
}
