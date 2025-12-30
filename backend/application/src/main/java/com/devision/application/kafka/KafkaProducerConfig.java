package com.devision.application.kafka.config;

import com.devision.application.kafka.event.ApplicationEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, ApplicationEvent> applicationEventProducerFactory(KafkaProperties props) {
        var config = new HashMap<String, Object>(props.buildProducerProperties());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Optional hardening (prod)
        // config.put(ProducerConfig.ACKS_CONFIG, "all");
        // config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ApplicationEvent> applicationEventKafkaTemplate(
            ProducerFactory<String, ApplicationEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}
