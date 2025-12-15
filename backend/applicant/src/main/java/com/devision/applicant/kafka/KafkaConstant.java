package com.devision.applicant.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConstant {
    public static final String APPLICANT_GROUP_ID = "applicant-service-id";
    public static final String KAFKA_PORT = "9092";
    public static final String KAFKA_HOST_URL = "localhost:" + KAFKA_PORT;

    public static final String RESPONSE = "-response";
    public static final String APPLICANT_TOPIC = "applicant-info";
    public static final String APPLICANT_TOPIC_RESPONSE = APPLICANT_TOPIC + RESPONSE;
}
