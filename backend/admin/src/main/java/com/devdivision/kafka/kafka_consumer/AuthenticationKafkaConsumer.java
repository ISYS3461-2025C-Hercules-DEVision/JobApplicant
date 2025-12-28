package com.devdivision.kafka.kafka_consumer;

import com.devdivision.config.KafkaConstant;
import com.devdivision.kafka.kafka_producer.KafkaGenericProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaGenericProducer<Object> kafkaGenericProducer;

    public AuthenticationKafkaConsumer(ObjectMapper objectMapper, KafkaGenericProducer<Object> kafkaGenericProducer) {
        this.objectMapper = objectMapper;
        this.kafkaGenericProducer = kafkaGenericProducer;
    }

    @KafkaListener(
            topics = KafkaConstant.ADMIN_AUTHENTICATION_TOPIC,
            groupId = KafkaConstant.ADMIN_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from AUTH: " + message);
    }
}
