package com.devision.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SubscriptionApplication
 *
 * Entry point for the Subscription microservice
 *
 * PURPOSE:
 * - Manages applicant premium subscriptions
 * - Integrates with Job Manager Payment Service
 */
@SpringBootApplication
public class SubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriptionApplication.class, args);
    }
}
