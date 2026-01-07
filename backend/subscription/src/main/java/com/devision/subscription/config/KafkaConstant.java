package com.devision.subscription.config;

public class KafkaConstant {
    public static final String SUBSCRIPTION_GROUP_ID = "subscription-service-id";
    public static final String KAFKA_HOST_URL = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");

    public static final String AUTHENTICATION_SUBSCRIPTION_TOPIC = "authentication-subscription-info";
}
