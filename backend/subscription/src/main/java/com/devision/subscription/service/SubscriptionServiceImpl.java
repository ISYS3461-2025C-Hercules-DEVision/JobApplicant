package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
    public SubscriptionStatusResponse getMySubscription(String applicantId) {

        return subscriptionRepository
                .findByApplicantIdAndIsActiveTrue(applicantId)
                .map(sub -> new SubscriptionStatusResponse(
                        sub.getPlanType(),
                        true,
                        sub.getExpiryDate()))
                .orElse(new SubscriptionStatusResponse(
                        PlanType.FREE,
                        false,
                        null));
    }

    @Override
    public PaymentInitiateResponseDTO createMockPayment(String applicantId) {

        // 1. Create payment transaction
        String paymentId = UUID.randomUUID().toString();

        PaymentTransaction tx = new PaymentTransaction();
        tx.setId(paymentId);
        tx.setApplicantId(applicantId);
        tx.setPaymentStatus(com.devision.subscription.enums.PaymentStatus.SUCCESS);
        tx.setTransactionTime(Instant.now());

        paymentTransactionRepository.save(tx);

        // 2. Deactivate old subscription (if any)
        subscriptionRepository
                .findByApplicantIdAndIsActiveTrue(applicantId)
                .ifPresent(old -> {
                    old.setActive(false);
                    subscriptionRepository.save(old);
                });

        // 3. Create new PREMIUM subscription
        Subscription sub = new Subscription();
        sub.setApplicantId(applicantId);
        sub.setPlanType(PlanType.PREMIUM);
        sub.setStartDate(Instant.now());
        sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
        sub.setActive(true);

        subscriptionRepository.save(sub);

        return new PaymentInitiateResponseDTO(
                paymentId,
                "SUCCESS",
                "Mock payment successful");
    }

    @Override
    public SubscriptionStatusResponse createDefaultSubscriptionForUser(String applicantId) {
        // If an active subscription already exists, return it
        return subscriptionRepository
                .findByApplicantIdAndIsActiveTrue(applicantId)
                .map(sub -> new SubscriptionStatusResponse(
                        sub.getPlanType(),
                        true,
                        sub.getExpiryDate()))
                .orElseGet(() -> {
                    // Create a FREE active subscription
                    Subscription sub = new Subscription();
                    sub.setApplicantId(applicantId);
                    sub.setPlanType(PlanType.FREE);
                    sub.setStartDate(Instant.now());
                    sub.setExpiryDate(null); // Free plan: no expiry
                    sub.setActive(true);

                    subscriptionRepository.save(sub);

                    return new SubscriptionStatusResponse(
                            PlanType.FREE,
                            true,
                            null);
                });
    }
}
