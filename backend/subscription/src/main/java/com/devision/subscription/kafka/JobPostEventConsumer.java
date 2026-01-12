package com.devision.subscription.kafka;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for job-post updates from JM.
 *
 * Attempts to parse multiple payload shapes to be resilient to producer schemas.
 * Enabled when `notification.consumer.enabled=true`.
 */
@Service
@ConditionalOnProperty(name = "notification.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class JobPostEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(JobPostEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public JobPostEventConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    /** Consumes job-post update messages and triggers evaluation. */
    @KafkaListener(topics = {"${kafka.topics.job-post-updates}", "${kafka.topics.job-updates:job-updates}"}, groupId = "subscription-service", containerFactory = "defaultKafkaListenerContainerFactory")
    public void onJobPostUpdate(String message) {
        log.info("Received job-post message: {}", message);

        // Try direct DTO first
        try {
            JobPostEventDTO event = objectMapper.readValue(message, JobPostEventDTO.class);
            if (event.getTitle() != null) {
                log.info("Parsed as JobPostEventDTO: jobId={} title={}", event.getJobId(), event.getTitle());
                notificationService.evaluateAndNotify(event);
                return;
            }
        } catch (Exception ignore) { }

        // Fallback #1: documented payload with different field names
        try {
            var jm = objectMapper.readTree(message);
            JobPostEventDTO converted = new JobPostEventDTO();
            converted.setJobId(jm.path("jobPostId").asText(null));
            converted.setTitle(jm.path("title").asText(null));
            converted.setCompany(jm.path("companyId").asText(null));
            if (jm.has("skillIds") && jm.get("skillIds").isArray()) {
                java.util.List<String> skills = new java.util.ArrayList<>();
                jm.get("skillIds").forEach(n -> { if (n.isTextual()) skills.add(n.asText()); });
                converted.setSkills(skills);
            }
            converted.setCountry(jm.path("country").asText(null));
            if (converted.getTitle() != null) {
                log.info("Parsed as JobPostEventPayload-like: jobId={} title={}", converted.getJobId(), converted.getTitle());
                notificationService.evaluateAndNotify(converted);
                return;
            }
        } catch (Exception ignore) { }

        // Fallback #2: JM JobPost entity
        try {
            var node = objectMapper.readTree(message);
            JobPostEventDTO converted = new JobPostEventDTO();
            converted.setJobId(node.path("id").asText(null));
            converted.setTitle(node.path("title").asText(null));
            converted.setCompany(node.path("companyId").asText(null));
            if (node.has("skills") && node.get("skills").isArray()) {
                java.util.List<String> skills = new java.util.ArrayList<>();
                node.get("skills").forEach(n -> { if (n.isTextual()) skills.add(n.asText()); });
                converted.setSkills(skills);
            }
            // JM uses 'location' for country value
            converted.setCountry(node.path("location").asText(null));

            if (converted.getTitle() != null) {
                log.info("Parsed as JM JobPost: jobId={} title={} location={}", converted.getJobId(), converted.getTitle(), converted.getCountry());
                notificationService.evaluateAndNotify(converted);
                return;
            }
        } catch (Exception ignore) { }

        log.warn("Unable to parse job-post message; skipping");
    }
}
