// package com.devision.subscription.payment;

// import com.devision.subscription.dto.PaymentEventDTO;
// import com.devision.subscription.dto.PaymentInitiateResponseDTO;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.UUID;

// @Service
// public class MockPaymentGateway implements PaymentGateway {

//     private final KafkaTemplate<String, PaymentEventDTO> kafkaTemplate;

//     public MockPaymentGateway(
//             KafkaTemplate<String, PaymentEventDTO> kafkaTemplate
//     ) {
//         this.kafkaTemplate = kafkaTemplate;
//     }

//     @Override
//     public PaymentInitiateResponseDTO initiatePayment(
//             String applicantId,
//             String email,
//             String subscriptionId
//     ) {
//         String paymentId = UUID.randomUUID().toString();

//         PaymentEventDTO event = new PaymentEventDTO();
//         event.setTransactionId(paymentId);
//         event.setCustomerId(applicantId);
//         event.setReferenceId(subscriptionId);
//         event.setAmount(BigDecimal.valueOf(10));
//         event.setCurrency("USD");
//         event.setGateway("MOCK");
//         event.setStatus("SUCCESS");
//         event.setEventType("SUCCESS");
//         event.setTimestamp(LocalDateTime.now());

//         kafkaTemplate.send("payment-success", applicantId, event);

//         return new PaymentInitiateResponseDTO(
//                 paymentId,
//                 "SUCCESS",
//                 "Mock payment successful"
//         );
//     }
// }
