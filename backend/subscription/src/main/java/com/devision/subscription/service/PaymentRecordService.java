package com.devision.subscription.service;

import com.devision.subscription.dto.PaymentRecordDTO;
import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentRecordService {

    private static final Logger log = LoggerFactory.getLogger(PaymentRecordService.class);

    private final PaymentTransactionRepository txRepo;
    private final SubscriptionRepository subRepo;

    public PaymentRecordService(PaymentTransactionRepository txRepo,
                                SubscriptionRepository subRepo) {
        this.txRepo = txRepo;
        this.subRepo = subRepo;
    }

    public PaymentTransaction saveFromCallback(PaymentRecordDTO dto) {
        log.info("Processing JM payment callback: {} status={} customer={}",
                dto.getTransactionId(), dto.getStatus(), dto.getCustomerId());

        // Idempotency: update existing or create new transaction
        Optional<PaymentTransaction> existing = txRepo.findById(dto.getTransactionId());
        PaymentTransaction tx = existing.orElseGet(PaymentTransaction::new);

        tx.setId(dto.getTransactionId());
        tx.setApplicantId(dto.getCustomerId());
        // Keep existing email if present (captured at initiation); no email in callback
        tx.setPaymentStatus(mapStatus(dto.getStatus()));
        tx.setTransactionTime(dto.getTimestamp() != null
                ? dto.getTimestamp().toInstant(ZoneOffset.UTC)
                : Instant.now());

        PaymentTransaction saved = txRepo.save(tx);

        // On SUCCESS, activate or extend subscription
        if (saved.getPaymentStatus() == PaymentStatus.SUCCESS) {
            activatePremium(saved.getApplicantId());
        }

        return saved;
    }

    public List<PaymentTransaction> getApplicantPayments(String applicantId) {
        return txRepo.findByApplicantId(applicantId);
    }

    private void activatePremium(String applicantId) {
        log.info("Activating PREMIUM subscription for applicant {}", applicantId);
        // Deactivate all active subscriptions
        subRepo.findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(applicantId).forEach(s -> {
            s.setActive(false);
            subRepo.save(s);
        });

        // Create/renew premium for 30 days
        Subscription sub = new Subscription();
        sub.setApplicantId(applicantId);
        sub.setPlanType(PlanType.PREMIUM);
        sub.setStartDate(Instant.now());
        sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
        sub.setActive(true);
        subRepo.save(sub);
    }

    private PaymentStatus mapStatus(String raw) {
        if (raw == null) return PaymentStatus.FAILED;
        return switch (raw.toUpperCase()) {
            case "SUCCESS", "SUCCEEDED", "PAID" -> PaymentStatus.SUCCESS;
            case "CREATED", "PENDING" -> PaymentStatus.CREATED;
            default -> PaymentStatus.FAILED;
        };
    }
}
