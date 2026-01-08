package com.devision.subscription.service;

import com.devision.subscription.client.PaymentInitiationClient;
import com.devision.subscription.dto.JmPaymentInitiateRequest;
import com.devision.subscription.dto.PaymentInitiateResponseDTO;
import com.devision.subscription.dto.SubscriptionStatusResponse;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.PaymentTransactionRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentInitiationClient paymentInitiationClient;
    @Value("${payment.forward.enabled:false}")
    private boolean forwardEnabled;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PaymentInitiationClient paymentInitiationClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentInitiationClient = paymentInitiationClient;
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
    public PaymentInitiateResponseDTO createMockPayment(String applicantId, String email) {

        if (forwardEnabled) {
            // Forward to JM Payment API and record CREATED transaction locally
            JmPaymentInitiateRequest req = new JmPaymentInitiateRequest();
            req.setSubsystem("JOB_APPLICANT");
            req.setPaymentType("SUBSCRIPTION");
            req.setCustomerId(applicantId);
            req.setEmail(email);
            req.setReferenceId("sub-" + applicantId);
            req.setAmount(java.math.BigDecimal.valueOf(10.00));
            req.setCurrency("USD");
            req.setGateway("STRIPE");
            req.setDescription("Premium Subscription");

            java.util.Map<String, Object> resp = paymentInitiationClient.initiate(req).block();
            String transactionId = resp != null && resp.get("transactionId") != null
                    ? resp.get("transactionId").toString()
                    : java.util.UUID.randomUUID().toString();
            String status = resp != null && resp.get("status") != null ? resp.get("status").toString() : "CREATED";

            com.devision.subscription.model.PaymentTransaction tx = new com.devision.subscription.model.PaymentTransaction();
            tx.setId(transactionId);
            tx.setApplicantId(applicantId);
            tx.setEmail(email);
            tx.setPaymentStatus(com.devision.subscription.enums.PaymentStatus.CREATED);
            tx.setTransactionTime(java.time.Instant.now());
            paymentTransactionRepository.save(tx);

            return new PaymentInitiateResponseDTO(
                    transactionId,
                    status,
                    "Initiated via JM Payment API");
        }

        // 1. Create payment transaction
        String paymentId = UUID.randomUUID().toString();

        PaymentTransaction tx = new PaymentTransaction();
        tx.setId(paymentId);
        tx.setApplicantId(applicantId);
        tx.setPaymentStatus(com.devision.subscription.enums.PaymentStatus.SUCCESS);
        tx.setEmail(email);
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
