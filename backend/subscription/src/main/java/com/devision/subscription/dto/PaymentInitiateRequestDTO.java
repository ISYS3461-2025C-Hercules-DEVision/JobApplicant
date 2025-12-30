package com.devision.subscription.dto;

public class PaymentInitiateRequestDTO {

    private String userId;
    private String userType;
    private String email;
    private int amount;

    public PaymentInitiateRequestDTO() {}

    public PaymentInitiateRequestDTO(String userId, String userType,
                                     String email, int amount) {
        this.userId = userId;
        this.userType = userType;
        this.email = email;
        this.amount = amount;
    }

    public String getUserId() { return userId; }
    public String getUserType() { return userType; }
    public String getEmail() { return email; }
    public int getAmount() { return amount; }
}
