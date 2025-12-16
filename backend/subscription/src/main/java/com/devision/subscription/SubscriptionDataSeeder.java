package com.devision.subscription;

import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class SubscriptionDataSeeder implements CommandLineRunner {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public void run(String... args) {

        if (subscriptionRepository.count() > 0) return;

        Instant now = Instant.now();

        String applicantId = "test-applicant-001";
        String subscriptionId = UUID.randomUUID().toString();

        // 1️⃣ Subscription
        Subscription subscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .applicantId(applicantId)
                .planType(PlanType.PREMIUM)
                .startDate(now)
                .expiryDate(now.plus(30, ChronoUnit.DAYS))
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        subscriptionRepository.save(subscription);

        // 2️⃣ Payment Transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .applicantId(applicantId)
                .email("testuser@gmail.com")
                .amount(new BigDecimal("199000"))
                .currency("VND")
                .paymentMethod("VNPAY")
                .status(PaymentStatus.SUCCESS)
                .timestamp(now)
                .subscriptionId(subscriptionId)
                .build();

        paymentTransactionRepository.save(transaction);

        System.out.println("✅ Seeded Subscription & PaymentTransaction");
        System.out.println("Subscription ID: " + subscriptionId);
        System.out.println("Applicant ID: " + applicantId);
    }
}
