package com.devdivision.config;

public class KafkaConstant {
    public static final String ADMIN_GROUP_ID = "applicant-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL =
            System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");




    // Admin->Authentication
    public static final String ADMIN_AUTHENTICATION_TOPIC = "admin-authentication-info";
    public static final String ADMIN_AUTHENTICATION_ADMIN_TOPIC_RESPONSE = ADMIN_AUTHENTICATION_TOPIC + RESPONSE;
}
