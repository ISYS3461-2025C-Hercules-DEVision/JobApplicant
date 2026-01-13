package com.devision.subscription.dto;

public class PaymentInitiateResponseDTO {

    private String paymentId;
    private String status;
    private String message;
    private String checkoutUrl; // optional: JM Stripe Checkout URL
    // JM-compatible fields
    private String paymentUrl; // redirect URL for Stripe Checkout (same as checkoutUrl)
    private String sessionId;  // Stripe session id

    public PaymentInitiateResponseDTO(String paymentId, String status, String message) {
        this(paymentId, status, message, null, null, null);
    }

    public PaymentInitiateResponseDTO(String paymentId, String status, String message,
                                      String checkoutUrl) {
        this(paymentId, status, message, checkoutUrl, null, null);
    }

    public PaymentInitiateResponseDTO(String paymentId, String status, String message,
                                      String checkoutUrl, String paymentUrl, String sessionId) {
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
        this.checkoutUrl = checkoutUrl;
        this.paymentUrl = paymentUrl;
        this.sessionId = sessionId;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getPaymentUrl() { return paymentUrl; }
    public String getSessionId() { return sessionId; }
}