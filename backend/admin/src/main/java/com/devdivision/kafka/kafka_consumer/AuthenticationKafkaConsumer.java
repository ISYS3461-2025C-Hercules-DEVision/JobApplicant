package com.devdivision.kafka.kafka_consumer;

import com.devdivision.config.KafkaConstant;
import com.devdivision.connection.AdminAuthenticationCodeWithUuid;
import com.devdivision.connection.AuthenticationAdminCodeWithUuid;
import com.devdivision.dto.AdminCreateRequestDTO;
import com.devdivision.dto.AdminDTO;
import com.devdivision.internal.entity.AdminRole;
import com.devdivision.internal.service.AdminService;
import com.devdivision.kafka.kafka_producer.KafkaGenericProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaGenericProducer<Object> kafkaGenericProducer;
    private final AdminService adminService;

    public AuthenticationKafkaConsumer(ObjectMapper objectMapper, KafkaGenericProducer<Object> kafkaGenericProducer, AdminService adminService) {
        this.objectMapper = objectMapper;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.adminService = adminService;
    }


    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_ADMIN_TOPIC,
            groupId = KafkaConstant.ADMIN_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message from AUTH: " + message);
        AuthenticationAdminCodeWithUuid adminCodeWithUuid = objectMapper.readValue(message, AuthenticationAdminCodeWithUuid.class);
        String correlationId = adminCodeWithUuid.correlationId();
        String adminEmail = adminCodeWithUuid.adminEmail();
        String adminRole = adminCodeWithUuid.adminRole();
        AdminRole role = AdminRole.fromString(adminRole);
        System.out.println("correlationId: " + correlationId);
        System.out.println("Admin Email: " + adminEmail);
        System.out.println("Admin Role: " + adminRole);


        AdminCreateRequestDTO adminCreateRequestDTO = new AdminCreateRequestDTO(adminEmail, role);
        AdminDTO create = adminService.createSuperAdmin(adminCreateRequestDTO);

        // send response back to Authentication
        AdminAuthenticationCodeWithUuid response =  new AdminAuthenticationCodeWithUuid(correlationId,create.adminEmail());

        kafkaGenericProducer.sendMessage(KafkaConstant.AUTHENTICATION_ADMIN_TOPIC_RESPONSE, response);
    }
}
