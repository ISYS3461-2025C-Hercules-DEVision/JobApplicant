package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.SubscriptionApplicantCodeWithUuid;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.Resume;
import com.devision.applicant.repository.ResumeRepository;
import com.devision.applicant.service.ApplicantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class SubscriptionKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final ResumeRepository resumeRepository;

    public SubscriptionKafkaConsumer(ObjectMapper objectMapper, ResumeRepository resumeRepository) {
        this.objectMapper = objectMapper;
        this.resumeRepository = resumeRepository;
    }

    @KafkaListener(
            topics = KafkaConstant.SUBSCRIPTION_SALARY_UPDATE_TOPIC,
            groupId = KafkaConstant.APPLICANT_GROUP_ID,
            containerFactory = "subscriptionKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from SUBSCRIPTION: " + message);
        SubscriptionApplicantCodeWithUuid applicant = objectMapper.readValue(message, SubscriptionApplicantCodeWithUuid.class);

        try{
            SubscriptionApplicantCodeWithUuid event = objectMapper.readValue(message, SubscriptionApplicantCodeWithUuid.class);

            log.info("Parsed event: applicantId={}, minSalary={}, maxSalary={}",
                    event.applicantId(), event.minSalary(), event.maxSalary());

            if(event.applicantId() == null || event.minSalary() == null || event.maxSalary() == null){
                log.warn("Invalid event - missing required fields: {}", event);
                return;
            }

            Resume resume = resumeRepository.findByApplicantId(event.applicantId()).orElse(null);

            if(resume == null){
                log.warn("No resume found for applicantId: {}", event.applicantId());
                return;
            }

            //Update salary fields
            resume.setMinSalary(event.minSalary());
            resume.setMaxSalary(event.maxSalary());
            resume.setUpdatedAt(Instant.now());

            //Save
            resumeRepository.save(resume);
            log.info("Salary expectations updated successfully for resumeId: {}", resume.getResumeId());
        }catch (JsonProcessingException e){
            log.error("Failed to parse event: {}", message, e);
        }catch (Exception e){
            log.error("Failed to update salary expectations for applicantId: {}", applicant.applicantId(), e);
        }
    }
}
