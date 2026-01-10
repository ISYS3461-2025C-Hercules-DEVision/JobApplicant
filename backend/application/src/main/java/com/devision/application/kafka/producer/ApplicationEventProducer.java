package com.devision.application.kafka.producer;

import com.devision.application.kafka.event.ApplicationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventProducer {

    private final KafkaTemplate<String, ApplicationEvent> kafkaTemplate;

    public ApplicationEventProducer(KafkaTemplate<String, ApplicationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, ApplicationEvent event) {
        // key = applicationId để giữ ordering theo từng application
        kafkaTemplate.send(topic, event.getApplicationId(), event);
    }
}
