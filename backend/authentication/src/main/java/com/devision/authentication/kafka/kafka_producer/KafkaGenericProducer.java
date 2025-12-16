package com.devision.authentication.kafka.kafka_producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaGenericProducer <T>{
    @Autowired
    KafkaTemplate<String, T> kafkaTemplate;
    public void sendMessage(String topic, T data){
        kafkaTemplate.send(topic, data);
    }
}
