package com.devision.subscription.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.devision.subscription.enums.PaymentStatus;


import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payment_transactions")
public class PaymentTransaction {

    @Id
    private String transactionId;     // UUID

    @Indexed
    private String applicantId;       // UUID

    private String email;

    private BigDecimal amount;        // Decimal128 in Mongo
    private String currency;          // e.g. "VND", "USD"
    private String paymentMethod;     // e.g. "MOMO", "VNPAY", "CARD"

    private PaymentStatus status;     // SUCCESS | FAILED
    private Instant timestamp;

    @Indexed
    private String subscriptionId;    // UUID (FK-like reference)
}
