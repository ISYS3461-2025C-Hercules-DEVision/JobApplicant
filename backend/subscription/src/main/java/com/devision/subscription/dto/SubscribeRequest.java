package com.devision.subscription.dto;

public class SubscribeRequest {

    private String email;

    public SubscribeRequest() {}
    public SubscribeRequest(String email) { this.email = email; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
