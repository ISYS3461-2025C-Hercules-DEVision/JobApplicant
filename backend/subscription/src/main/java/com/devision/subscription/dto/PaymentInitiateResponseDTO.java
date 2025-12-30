package com.devision.subscription.dto;

public class PaymentInitiateResponseDTO {

    private String paymentId;
    private String status;
    private String message;

    public PaymentInitiateResponseDTO() {}

    public PaymentInitiateResponseDTO(String paymentId, String status, String message) {
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
