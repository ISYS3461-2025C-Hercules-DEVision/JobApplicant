package com.devision.subscription.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.devision.subscription.enums.PaymentStatus;
import com.devision.subscription.model.PaymentTransaction;

import java.util.List;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {

    List<PaymentTransaction> findByApplicantId(String applicantId);

    long countByApplicantIdAndPaymentStatus(String applicantId, PaymentStatus paymentStatus);

}
