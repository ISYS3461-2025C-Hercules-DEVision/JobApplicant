package com.devdivision.kafka.kafka_consumer;

import com.devdivision.config.KafkaConstant;
import com.devdivision.connection.ApplicantForAdminAuthenticationConnection.ApplicantForAdminAuthenticationCodeWithUuid;
import com.devdivision.connection.ApplicantForAdminAuthenticationConnection.AuthenticationApplicantForAdminCodeWithUuid;
import com.devdivision.dto.AdminAuthenticationDtos.AdminCreateRequestDTO;
import com.devdivision.dto.AdminAuthenticationDtos.AdminDTO;

import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminCreateRequestDto;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminDto;
import com.devdivision.internal.service.AdminService;
import com.devdivision.kafka.kafka_producer.KafkaGenericProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApplicantForAdminKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaGenericProducer<Object> kafkaGenericProducer;
    private final AdminService adminService;

    public AuthenticationApplicantForAdminKafkaConsumer(ObjectMapper objectMapper, KafkaGenericProducer<Object> kafkaGenericProducer, AdminService adminService) {
        this.objectMapper = objectMapper;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.adminService = adminService;
    }


    @KafkaListener(
            topics = KafkaConstant.APPLICANT_FOR_ADMIN_AUTHENTICATION_TOPIC_RESPONSE,
            groupId = KafkaConstant.ADMIN_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from AUTH: " + message);
        AuthenticationApplicantForAdminCodeWithUuid applicantForAdminCodeWithUuid = objectMapper.readValue(message, AuthenticationApplicantForAdminCodeWithUuid.class);
        String correlationId = applicantForAdminCodeWithUuid.correlationId();
        String email = applicantForAdminCodeWithUuid.email();
        String fullName = applicantForAdminCodeWithUuid.fullName();
        String phoneNumber = applicantForAdminCodeWithUuid.phoneNumber();
        String country = applicantForAdminCodeWithUuid.country();
        System.out.println("correlationId: " + correlationId);
        System.out.println("Admin Email: " + email);
        System.out.println("Full Name: " + fullName);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Country: " + country);



        ApplicantForAdminCreateRequestDto req = new ApplicantForAdminCreateRequestDto(email, fullName, phoneNumber, country);
        ApplicantForAdminDto create = adminService.createApplicantForAdmin(req);

        // send response back to Authentication
        ApplicantForAdminAuthenticationCodeWithUuid response =  new ApplicantForAdminAuthenticationCodeWithUuid(correlationId, create.id());

        kafkaGenericProducer.sendMessage(KafkaConstant.APPLICANT_FOR_ADMIN_AUTHENTICATION_TOPIC_RESPONSE, response);
    }
}
