package com.devision.subscription.dto;

public class CheckoutResponse {

    private String checkoutUrl;

    public CheckoutResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }
}
