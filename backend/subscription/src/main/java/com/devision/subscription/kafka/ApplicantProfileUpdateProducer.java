package com.devision.subscription.kafka;

import com.devision.subscription.dto.ApplicantProfileUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class ApplicantProfileUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public ApplicantProfileUpdateProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.applicant-profile-updates:subscription-salary-update}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishSalaryUpdate(String applicantId, BigDecimal minSalary, BigDecimal maxSalary) {
        ApplicantProfileUpdateEvent event = new ApplicantProfileUpdateEvent(
                applicantId,
                minSalary,
                maxSalary,
                Instant.now());
        kafkaTemplate.send(topic, applicantId, event);
    }
}
