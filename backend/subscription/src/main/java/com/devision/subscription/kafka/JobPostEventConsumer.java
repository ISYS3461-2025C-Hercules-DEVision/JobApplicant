package com.devision.subscription.kafka;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for job-post updates from JM.
 *
 * For each incoming event, delegates to {@code NotificationService} to
 * evaluate all stored Search Profiles and persist notifications for matched
 * active PREMIUM subscribers.
 *
 * Enabled when `notification.consumer.enabled=true`.
 */
@Service
@ConditionalOnProperty(name = "notification.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class JobPostEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public JobPostEventConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    /** Consumes job-post update messages and triggers evaluation. */
    @KafkaListener(topics = "${kafka.topics.job-post-updates}", groupId = "subscription-service", containerFactory = "defaultKafkaListenerContainerFactory")
    public void onJobPostUpdate(String message) throws Exception {
        JobPostEventDTO event = objectMapper.readValue(message, JobPostEventDTO.class);
        notificationService.evaluateAndNotify(event);
    }
}
