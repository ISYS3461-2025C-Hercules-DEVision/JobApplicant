package com.devision.subscription.dto;

import java.math.BigDecimal;

/**
 * Request payload sent to JM Payment API to initiate a subscription payment.
 * Designed to be forward-compatible with JM: subsystem, paymentType, and
 * customerId are used for routing and reconciliation.
 */
public class JmPaymentInitiateRequest {
    private String subsystem; // JOB_APPLICANT
    private String paymentType; // SUBSCRIPTION
    private String customerId; // applicantId
    private String email; // applicant email
    private String referenceId; // subscription id or synthetic ref
    private BigDecimal amount; // 10.00
    private String currency; // USD
    private String gateway; // STRIPE
    private String description; // optional

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
