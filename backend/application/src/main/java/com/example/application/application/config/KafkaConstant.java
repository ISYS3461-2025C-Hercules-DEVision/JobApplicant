package com.example.application.application.config;

public class KafkaConstant {
    public static final String APPLICATION_GROUP_ID = "application-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL =
            System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");




    //Application -> Company
    public static final String APPLICATION_COMPANY_TOPIC = "application-status-info";
    public static final String APPLICATION_COMPANY_TOPIC_RESPONSE = APPLICATION_COMPANY_TOPIC + RESPONSE;


}
