// package com.devision.subscription.kafka;

// import com.devision.subscription.dto.PaymentEventDTO;
// import com.devision.subscription.enums.PaymentStatus;
// import com.devision.subscription.enums.PlanType;
// import com.devision.subscription.model.PaymentTransaction;
// import com.devision.subscription.model.Subscription;
// import com.devision.subscription.repository.PaymentTransactionRepository;
// import com.devision.subscription.repository.SubscriptionRepository;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;

// @Service
// public class PaymentEventConsumer {

//     private final SubscriptionRepository subscriptionRepository;
//     private final PaymentTransactionRepository paymentTransactionRepository;

//     public PaymentEventConsumer(
//             SubscriptionRepository subscriptionRepository,
//             PaymentTransactionRepository paymentTransactionRepository
//     ) {
//         this.subscriptionRepository = subscriptionRepository;
//         this.paymentTransactionRepository = paymentTransactionRepository;
//     }

//     @KafkaListener(
//         topics = "${kafka.topics.payment-success}",
//         groupId = "subscription-service"
//     )
//     public void onPaymentSuccess(PaymentEventDTO event) {

//         PaymentTransaction tx =
//             paymentTransactionRepository.findById(event.getTransactionId())
//                 .orElseThrow();

//         tx.setPaymentStatus(PaymentStatus.SUCCESS);
//         paymentTransactionRepository.save(tx);

//         subscriptionRepository
//             .findByApplicantIdAndIsActiveTrue(event.getCustomerId())
//             .ifPresent(old -> {
//                 old.setActive(false);
//                 subscriptionRepository.save(old);
//             });

//         Subscription sub = new Subscription();
//         sub.setApplicantId(event.getCustomerId());
//         sub.setPlanType(PlanType.PREMIUM);
//         sub.setStartDate(Instant.now());
//         sub.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
//         sub.setActive(true);

//         subscriptionRepository.save(sub);
//     }
// }
