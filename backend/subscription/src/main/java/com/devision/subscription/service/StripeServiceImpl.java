package com.devision.subscription.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.price-usd}")
    private Long priceUsd;

    @Override
    public String createCheckoutSession(String applicantId, String email) {
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setCustomerEmail(email)
                            .setSuccessUrl("http://localhost:3000/subscription")
                            .setCancelUrl("http://localhost:3000/subscription")
                            .putMetadata("applicantId", applicantId)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("usd")
                                                            .setUnitAmount(priceUsd)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Premium Applicant Subscription")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            return Session.create(params).getUrl();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
