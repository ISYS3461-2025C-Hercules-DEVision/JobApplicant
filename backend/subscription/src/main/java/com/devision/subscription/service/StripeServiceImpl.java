package com.devision.subscription.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.price-usd}")
    private Long priceUsd;

    @Override
    public String createCheckoutSession(String applicantId, String email) {

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomerEmail(email)
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setSuccessUrl("http://localhost:3000/payment-success")
                        .setCancelUrl("http://localhost:3000/payment-cancel")
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
                                                                        .setName("Premium Applicant Subscription (30 days)")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        try {
            Session session = Session.create(params);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }
}
