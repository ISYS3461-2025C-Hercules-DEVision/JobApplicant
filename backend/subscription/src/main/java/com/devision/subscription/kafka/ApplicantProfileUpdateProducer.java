package com.devision.subscription.kafka;

import com.devision.subscription.dto.ApplicantProfileUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ============================================================
 * APPLICANT PROFILE UPDATE PRODUCER - KAFKA EVENT PUBLISHER
 * ============================================================
 *
 * PURPOSE:
 * - Publishes salary range updates (linked to premium subscription) to Kafka.
 * - Allows applicant service to subscribe and update indexed profiles in
 * real-time.
 * - Enables microservice-level data consistency without direct DB updates.
 *
 * KAFKA TOPIC:
 * - kafka.topics.applicant-profile-updates (default:
 * "subscription-salary-update")
 * - Topic partitioning: By applicantId (key) -> ensures order per applicant.
 *
 * FLOW:
 * 1. User updates salary expectations in profile (on frontend).
 * 2. Profile service calls subscriptionService.publishSalaryUpdate(applicantId,
 * minSalary, maxSalary).
 * 3. Producer serializes event to Kafka topic:
 * { applicantId, minSalary, maxSalary, eventTime }.
 * 4. Applicant service consumes and updates Elasticsearch/DB indices.
 *
 * EVENT SCHEMA:
 * - applicantId (String): Subscriber ID (used as Kafka message key).
 * - minSalary (BigDecimal): Minimum expected salary.
 * - maxSalary (BigDecimal): Maximum expected salary.
 * - eventTime (Instant): When event was published (ISO 8601).
 *
 * USAGE:
 * - Injected into service classes that need to publish profile updates.
 * - Called after subscription state changes (if salary visibility tied to
 * premium).
 *
 * INTEGRATION:
 * - Sender: Subscription service (this component).
 * - Receiver: Applicant service (AuthenticationSubscriptionKafkaConsumer).
 * - Data flow: Subscription -> Kafka -> Applicant -> Search indices.
 *
 * NOTES:
 * - No error handling; failures logged but not retried (async fire-and-forget).
 * - Message key = applicantId ensures partitioning and order per applicant.
 * - Consider adding callback for production (track success/failure metrics).
 * ============================================================
 */
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
