package com.devision.applicant.config;

public class KafkaConstant {
    public static final String APPLICANT_GROUP_ID = "applicant-service-id";
    public static final String RESPONSE = "-response";
    public static final String KAFKA_HOST_URL =
            System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");




    // Applicant -> Authentication
    public static final String APPLICANT_AUTHENTICATION_TOPIC = "applicant-authentication-info";
    public static final String APPLICANT_AUTHENTICATION_TOPIC_RESPONSE = APPLICANT_AUTHENTICATION_TOPIC + RESPONSE;



    //Response message from JM when applicant updates Country or Skills
    public static final String PROFILE_UPDATE_TOPIC = "applicant-profile-updates";
    public static final String PROFILE_UPDATE_RESPONSE = PROFILE_UPDATE_TOPIC + RESPONSE;

}
