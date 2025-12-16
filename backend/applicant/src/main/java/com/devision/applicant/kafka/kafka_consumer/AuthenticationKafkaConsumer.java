package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantAutheticationCodeWithUuid;
import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.AutheticationApplicantCodeWithUuid;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.service.ApplicantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaGenericProducer<Object> kafkaGenericProducer;
    private final ApplicantService applicantService;

    public AuthenticationKafkaConsumer(ObjectMapper objectMapper,
                                       KafkaGenericProducer<Object> kafkaGenericProducer,
                                       ApplicantService applicantService) {
        this.objectMapper = objectMapper;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.applicantService = applicantService;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_TOPIC,
            groupId = KafkaConstant.APPLICANT_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from AUTH: " + message);

        // Deserialize AuthToApplicantEvent
        AutheticationApplicantCodeWithUuid applicant = objectMapper.readValue(message, AutheticationApplicantCodeWithUuid.class);


        String correlationId = applicant.getCorrelationId();
        String email = applicant.getEmail();
        String fullName = applicant.getFullName();
        System.out.println("correlationId: " + correlationId);
        System.out.println("email: " + email);
        System.out.println("fullName: " + fullName);
        // Create Applicant from this data
        ApplicantCreateRequest req = new ApplicantCreateRequest(
                fullName,
                email,
                null,   // country
                null,   // city
                null,   // streetAddress
                null,   // phoneNumber
                null    // profileImageUrl
        );

        ApplicantDTO created = applicantService.create(req);

        // Build response DTO with correlationId + applicantId
        ApplicantAutheticationCodeWithUuid response =
                new ApplicantAutheticationCodeWithUuid(
                        correlationId,
                        created.applicantId(),
                        created.email()
                );

        // Send response back to AUTHENTICATION_TOPIC_RESPONSE
        kafkaGenericProducer.sendMessage(
                KafkaConstant.AUTHENTICATION_TOPIC_RESPONSE,
                response
        );
    }
}
