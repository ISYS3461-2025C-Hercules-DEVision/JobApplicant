package com.devdivision.config;

public class KafkaConstant {
    public static final String ADMIN_GROUP_ID = "admin-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL =
            System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");




    // SuperAdmin->Authentication
    public static final String AUTHENTICATION_ADMIN_TOPIC = "authentication-admin-info";
    public static final String AUTHENTICATION_ADMIN_TOPIC_RESPONSE = AUTHENTICATION_ADMIN_TOPIC + "-response";

    // Applicant For Admin -> Authentication
    public static final String APPLICANT_FOR_ADMIN_AUTHENTICATION_TOPIC = "authentication-applicant-for-admin-info";
    public static final String APPLICANT_FOR_ADMIN_AUTHENTICATION_TOPIC_RESPONSE = APPLICANT_FOR_ADMIN_AUTHENTICATION_TOPIC + RESPONSE;


}
