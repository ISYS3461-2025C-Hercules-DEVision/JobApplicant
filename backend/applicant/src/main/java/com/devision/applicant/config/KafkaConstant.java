package com.devision.applicant.config;

public class KafkaConstant {
    public static final String APPLICANT_GROUP_ID = "applicant-service-id";
    public static final String KAFKA_PORT = "9092";
    public static final String KAFKA_HOST_URL = "localhost:" + KAFKA_PORT;

    public static final String RESPONSE = "-response";
    public static final String AUTHENTICATION_TOPIC = "authentication-info";
    public static final String AUTHENTICATION_TOPIC_RESPONSE = AUTHENTICATION_TOPIC + RESPONSE;

    public static final String APPLICATION_TOPIC = "application-info";
    public static final String APPLICATION_TOPIC_RESPONSE = APPLICATION_TOPIC + RESPONSE;
}
