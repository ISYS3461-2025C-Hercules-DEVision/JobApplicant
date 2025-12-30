package com.devision.subscription.model;

import com.devision.subscription.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payment_transactions")
public class PaymentTransaction {

    @Id
    private String id;
    private String applicantId;
    private String email;
    private PaymentStatus paymentStatus;
    private Instant transactionTime;
    private String paymentId;

    public PaymentTransaction() {}

    public PaymentTransaction(String applicantId, String email,
                              PaymentStatus paymentStatus,
                              Instant transactionTime, String paymentId) {
        this.applicantId = applicantId;
        this.email = email;
        this.paymentStatus = paymentStatus;
        this.transactionTime = transactionTime;
        this.paymentId = paymentId;
    }

    public String getApplicantId() { return applicantId; }
    public String getEmail() { return email; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public Instant getTransactionTime() { return transactionTime; }
    public String getPaymentId() { return paymentId; }

    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    public void setEmail(String email) { this.email = email; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setTransactionTime(Instant transactionTime) { this.transactionTime = transactionTime; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
