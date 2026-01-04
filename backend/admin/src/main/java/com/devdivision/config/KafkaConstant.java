package com.devdivision.config;

public class KafkaConstant {
    public static final String ADMIN_GROUP_ID = "admin-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL =
            System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");




    // Admin->Authentication
    public static final String AUTHENTICATION_ADMIN_TOPIC = "authentication-admin-info";
    public static final String AUTHENTICATION_ADMIN_TOPIC_RESPONSE = AUTHENTICATION_ADMIN_TOPIC + "-response";
}
