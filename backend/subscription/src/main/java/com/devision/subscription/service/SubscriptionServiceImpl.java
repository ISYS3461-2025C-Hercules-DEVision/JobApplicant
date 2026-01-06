package com.devision.subscription.service;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PaymentTransactionRepository paymentTransactionRepository
    ) {
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
    public PaymentInitiateResponseDTO startSubscription(
            String applicantId,
            String email
    ) {
        String paymentId = UUID.randomUUID().toString();

        // Record transaction (Simplex 5.1.2)
        PaymentTransaction tx = new PaymentTransaction();
        tx.setApplicantId(applicantId);
        tx.setEmail(email);
        tx.setPaymentStatus(PaymentStatus.PENDING);
        tx.setTransactionTime(Instant.now());
        tx.setStripeSessionId(paymentId);

        paymentTransactionRepository.save(tx);

        return new PaymentInitiateResponseDTO(
                paymentId,
                "PENDING",
                "Mock payment created"
        );
    }

    @Override
    public void markSubscriptionPaid(String applicantId) {

        Subscription sub = new Subscription();
        sub.setApplicantId(applicantId);
        sub.setPlanType(PlanType.PREMIUM);
        sub.setActive(true);
        sub.setStartDate(Instant.now());
        sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));

        subscriptionRepository.save(sub);
    }
}
