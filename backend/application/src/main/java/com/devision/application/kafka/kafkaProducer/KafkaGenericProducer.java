package com.devision.application.kafka.kafkaProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaGenericProducer<T>{
    @Autowired
    private KafkaTemplate<String, T> kafkaTemplate;

    public void sendMessage(String topic, T message) {
        kafkaTemplate.send(topic, message);
    }
}
