package com.devision.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing payment events emitted by JM Payment system. Used by
 * Kafka consumers to update local transactions and subscriptions.
 */
public class PaymentEventDTO {

    private String transactionId;
    private String subsystem;
    private String paymentType;
    private String customerId; // applicantId
    private String referenceId; // subscriptionId
    private BigDecimal amount;
    private String currency;
    private String gateway;
    private String status; // SUCCESS | FAILED
    private LocalDateTime timestamp;
    private String eventType; // INITIATED | SUCCESS | FAILED

    public PaymentEventDTO() {
    }

    // getters & setters
    public PaymentEventDTO(
            String transactionId,
            String subsystem,
            String paymentType,
            String customerId,
            String referenceId,
            BigDecimal amount,
            String currency,
            String gateway,
            String status,
            LocalDateTime timestamp,
            String eventType) {
        this.transactionId = transactionId;
        this.subsystem = subsystem;
        this.paymentType = paymentType;
        this.customerId = customerId;
        this.referenceId = referenceId;
        this.amount = amount;
        this.currency = currency;
        this.gateway = gateway;
        this.status = status;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getGateway() {
        return gateway;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
