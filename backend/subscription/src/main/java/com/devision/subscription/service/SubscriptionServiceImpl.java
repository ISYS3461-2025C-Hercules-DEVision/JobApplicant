package com.devision.subscription.service;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PaymentTransactionRepository paymentTransactionRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public Subscription getActiveSubscription(String applicantId) {
        return subscriptionRepository
                .findByApplicantIdAndIsActiveTrue(applicantId)
                .orElse(null);
    }

    @Override
    public void activatePremium(String applicantId, String email,
                                String paymentId, Instant transactionTime) {

        paymentTransactionRepository.save(
                new PaymentTransaction(
                        applicantId,
                        email,
                        PaymentStatus.SUCCESS,
                        transactionTime,
                        paymentId
                )
        );

        subscriptionRepository.save(
                new Subscription(
                        applicantId,
                        PlanType.PREMIUM,
                        Instant.now(),
                        Instant.now().plus(30, ChronoUnit.DAYS),
                        true
                )
        );
    }
}
