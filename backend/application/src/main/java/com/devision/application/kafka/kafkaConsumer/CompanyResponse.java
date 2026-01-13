package com.devision.application.kafka.kafkaConsumer;

import com.devision.application.config.KafkaConstant;
import com.devision.application.dto.CompanyResponseDTO;
import com.devision.application.kafka.kafkaProducer.KafkaGenericProducer;

import com.devision.application.model.Application;
import com.devision.application.service.ApplicationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CompanyResponse {

    private final ObjectMapper objectMapper;
    private final KafkaGenericProducer<Object> kafkaGenericProducer;
    private final ApplicationService applicationService;

    public CompanyResponse(ObjectMapper objectMapper, KafkaGenericProducer<Object> kafkaGenericProducer, ApplicationService applicationService) {
        this.objectMapper = objectMapper;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.applicationService = applicationService;
    }


    @KafkaListener(
            topics = KafkaConstant.APPLICATION_COMPANY_TOPIC_RESPONSE,
            groupId = KafkaConstant.APPLICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from AUTH: " + message);
        CompanyResponseDTO companyResponseDTO = objectMapper.readValue(message, CompanyResponseDTO.class);
        String jobPostId= companyResponseDTO.jobPostId();
        String status= companyResponseDTO.status();
        String feedback= companyResponseDTO.feedback();
        String applicationId = companyResponseDTO.applicationId();
        applicationService.updateApplicationStatus(jobPostId,status,feedback,applicationId);
    }
}
