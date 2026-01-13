package com.devision.subscription.repository;

import com.devision.subscription.model.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * PaymentTransactionRepository
 *
 * PURPOSE:
 * - Stores lightweight payment records
 *
 * SRS MAPPING:
 * - Requirement 5.1.2:
 * Record payment transaction with email and transaction time
 */
public interface PaymentTransactionRepository
                extends MongoRepository<PaymentTransaction, String> {

        java.util.Optional<PaymentTransaction> findByStripeSessionId(String stripeSessionId);
}
