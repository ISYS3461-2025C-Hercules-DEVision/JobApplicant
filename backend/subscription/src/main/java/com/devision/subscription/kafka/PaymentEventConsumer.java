package com.devision.subscription.kafka;

import com.devision.subscription.dto.PaymentEventDTO;
import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Kafka consumer for payment lifecycle events emitted by JM Payment.
 *
 * Listens to three topics:
 * - payment-success: marks transaction SUCCESS and provisions PREMIUM
 * - payment-initiated: records CREATED to link REST initiation with events
 * - payment-failed: marks transaction FAILED without changing subscriptions
 *
 * Enabled when `payment.consumer.enabled=true`.
 */
@Service
@ConditionalOnProperty(name = "payment.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class PaymentEventConsumer {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(
            SubscriptionRepository subscriptionRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            ObjectMapper objectMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.objectMapper = objectMapper;
    }

    /** Handles SUCCESS events for Job Applicant subscription payments. */
    @KafkaListener(topics = "${kafka.topics.payment-success}", groupId = "subscription-service", containerFactory = "defaultKafkaListenerContainerFactory")
    public void onPaymentSuccess(String message) throws Exception {
        PaymentEventDTO event = objectMapper.readValue(message, PaymentEventDTO.class);

        // Only handle Job Applicant subscription success
        if (!"JOB_APPLICANT".equalsIgnoreCase(event.getSubsystem()))
            return;
        if (!"SUBSCRIPTION".equalsIgnoreCase(event.getPaymentType()))
            return;
        if (!"SUCCESS".equalsIgnoreCase(event.getStatus()))
            return;

        PaymentTransaction tx = paymentTransactionRepository.findById(event.getTransactionId())
                .orElseGet(() -> {
                    PaymentTransaction p = new PaymentTransaction();
                    p.setId(event.getTransactionId());
                    p.setApplicantId(event.getCustomerId());
                    return p;
                });

        tx.setPaymentStatus(PaymentStatus.SUCCESS);
        if (event.getTimestamp() != null) {
            tx.setTransactionTime(event.getTimestamp().atZone(java.time.ZoneOffset.UTC).toInstant());
        } else {
            tx.setTransactionTime(Instant.now());
        }
        paymentTransactionRepository.save(tx);

        // Deactivate all current actives to avoid duplicates
        subscriptionRepository
                .findByApplicantIdAndIsActiveTrueOrderByStartDateDesc(event.getCustomerId())
                .forEach(old -> {
                    old.setActive(false);
                    subscriptionRepository.save(old);
                });

        Subscription sub = new Subscription();
        sub.setApplicantId(event.getCustomerId());
        sub.setPlanType(PlanType.PREMIUM);
        sub.setStartDate(Instant.now());
        sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
        sub.setActive(true);

        subscriptionRepository.save(sub);
    }

    /** Records CREATED status for initiated payments to aid reconciliation. */
    @KafkaListener(topics = "${kafka.topics.payment-initiated}", groupId = "subscription-service", containerFactory = "defaultKafkaListenerContainerFactory")
    public void onPaymentInitiated(String message) throws Exception {
        PaymentEventDTO event = objectMapper.readValue(message, PaymentEventDTO.class);

        if (!"JOB_APPLICANT".equalsIgnoreCase(event.getSubsystem()))
            return;
        if (!"SUBSCRIPTION".equalsIgnoreCase(event.getPaymentType()))
            return;
        if (!"INITIATED".equalsIgnoreCase(event.getEventType()))
            return;

        PaymentTransaction tx = paymentTransactionRepository.findById(event.getTransactionId())
                .orElseGet(() -> {
                    PaymentTransaction p = new PaymentTransaction();
                    p.setId(event.getTransactionId());
                    p.setApplicantId(event.getCustomerId());
                    return p;
                });

        tx.setPaymentStatus(PaymentStatus.CREATED);
        if (event.getTimestamp() != null) {
            tx.setTransactionTime(event.getTimestamp().atZone(java.time.ZoneOffset.UTC).toInstant());
        } else {
            tx.setTransactionTime(Instant.now());
        }
        paymentTransactionRepository.save(tx);
    }

    /** Handles FAILED events; stores status but does not alter subscriptions. */
    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "subscription-service", containerFactory = "defaultKafkaListenerContainerFactory")
    public void onPaymentFailed(String message) throws Exception {
        PaymentEventDTO event = objectMapper.readValue(message, PaymentEventDTO.class);

        // Only handle Job Applicant subscription failures
        if (!"JOB_APPLICANT".equalsIgnoreCase(event.getSubsystem()))
            return;
        if (!"SUBSCRIPTION".equalsIgnoreCase(event.getPaymentType()))
            return;
        if (!"FAILED".equalsIgnoreCase(event.getStatus()))
            return;

        PaymentTransaction tx = paymentTransactionRepository.findById(event.getTransactionId())
                .orElseGet(() -> {
                    PaymentTransaction p = new PaymentTransaction();
                    p.setId(event.getTransactionId());
                    p.setApplicantId(event.getCustomerId());
                    return p;
                });

        tx.setPaymentStatus(PaymentStatus.FAILED);
        if (event.getTimestamp() != null) {
            tx.setTransactionTime(event.getTimestamp().atZone(java.time.ZoneOffset.UTC).toInstant());
        } else {
            tx.setTransactionTime(Instant.now());
        }
        paymentTransactionRepository.save(tx);
        // Do not modify subscriptions on failure.
    }
}
