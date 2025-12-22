package com.devision.subscription.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.devision.subscription.enums.PlanType;


import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String subscriptionId;   // UUID

    @Indexed
    private String applicantId;      // UUID

    private PlanType planType;       // FREE | PREMIUM

    private Instant startDate;
    private Instant expiryDate;

    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant endAt;
}
