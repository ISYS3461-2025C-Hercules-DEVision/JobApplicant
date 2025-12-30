package com.devision.subscription.dto;

import lombok.Data;

@Data
public class CreateCheckoutSessionRequest {
    private String applicantId;
    private String email;
}
