package com.devision.applicant.kafka.kafka_producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaGenericProducer<T> {
    @Autowired
    private KafkaTemplate<String, T> kafkaTemplate;

    public void sendMessage(String topic, String key, T message) {
        kafkaTemplate.send(topic, key, message);
    }
}
