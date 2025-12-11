package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicantConsumer {
    private final ObjectMapper mapper;
    public ApplicantConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_TOPIC_RESPONSE,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleApplicantApiResponse(String record) {

    }

}
