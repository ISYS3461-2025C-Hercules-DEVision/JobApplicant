package com.devision.authentication.config;

public class KafkaConstant {
    public static final String AUTHENTICATION_GROUP_ID = "authentication-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");

    // Authentication -> Applicant
    public static final String AUTHENTICATION_APPLICANT_TOPIC = "authentication-applicant-info";
    public static final String AUTHENTICATION_APPLICANT_TOPIC_RESPONSE = AUTHENTICATION_APPLICANT_TOPIC + RESPONSE;

    // Authentication -> Admin
    public static final String AUTHENTICATION_ADMIN_TOPIC = "authentication-admin-info";
    public static final String AUTHENTICATION_ADMIN_TOPIC_RESPONSE = AUTHENTICATION_ADMIN_TOPIC + RESPONSE;

    // Applicant change status ->
    public static final String AUTHENTICATION_APPLICANT_CHANGE_STATUS_TOPIC = "authentication-applicant-change-status-info";

    // Authentication -> Subscription (provision default plan)
    public static final String AUTHENTICATION_SUBSCRIPTION_TOPIC = "authentication-subscription-info";
}
