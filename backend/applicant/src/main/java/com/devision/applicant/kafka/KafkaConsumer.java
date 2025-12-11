package com.devision.applicant.kafka;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.dto.AutheticationApplicantCodeWithUuid;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumer {
    @Bean
    public ConsumerFactory<String, AutheticationApplicantCodeWithUuid> defaultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.KAFKA_HOST_URL);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.APPLICANT_GROUP_ID);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AutheticationApplicantCodeWithUuid> defaultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AutheticationApplicantCodeWithUuid> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultConsumerFactory());
        return factory;
    }
}
