package com.devision.authentication.config;

public class KafkaConstant {
    public static final String AUTHENTICATION_GROUP_ID = "authentication-service-id";
    public static final String KAFKA_PORT = "9092";
    public static final String KAFKA_HOST_URL = "localhost:" + KAFKA_PORT;

    public static final String RESPONSE = "-response";
    public static final String AUTHENTICATION_TOPIC = "authentication-info";
    public static final String AUTHENTICATION_TOPIC_RESPONSE = AUTHENTICATION_TOPIC + RESPONSE;
}
