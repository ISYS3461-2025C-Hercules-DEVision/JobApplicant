package com.devision.subscription.kafka.kafka_consumer;

import com.devision.subscription.config.KafkaConstant;
import com.devision.subscription.connection.AuthToSubscriptionEvent;
import com.devision.subscription.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSubscriptionKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final SubscriptionService subscriptionService;

    public AuthenticationSubscriptionKafkaConsumer(ObjectMapper objectMapper,
            SubscriptionService subscriptionService) {
        this.objectMapper = objectMapper;
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = KafkaConstant.AUTHENTICATION_SUBSCRIPTION_TOPIC, groupId = KafkaConstant.SUBSCRIPTION_GROUP_ID, containerFactory = "defaultKafkaListenerContainerFactory")
    public void consume(String message) throws Exception {
        AuthToSubscriptionEvent event = objectMapper.readValue(message, AuthToSubscriptionEvent.class);
        String applicantId = event.getApplicantId();
        subscriptionService.createDefaultSubscriptionForUser(applicantId);
    }
}
