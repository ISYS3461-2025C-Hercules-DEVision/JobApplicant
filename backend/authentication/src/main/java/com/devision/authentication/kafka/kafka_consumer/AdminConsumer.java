package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AutheticationAdminCodeWithUuid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminConsumer {
    private final ObjectMapper mapper;
    private PendingApplicantRequests pendingApplicantRequests;
    public AdminConsumer(ObjectMapper mapper, PendingApplicantRequests pendingApplicantRequests) {
        this.pendingApplicantRequests = pendingApplicantRequests;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_ADMIN_TOPIC_RESPONSE,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleAdminApiResponse(String record) throws Exception {
        System.out.println("Received response from APPLICANT: " + record);
        AutheticationAdminCodeWithUuid payload =
                mapper.readValue(record, AutheticationAdminCodeWithUuid.class);
        String correlationId = payload.getCorrelationId();
        pendingApplicantRequests.complete(correlationId, payload);

        System.out.println("Completed pending request for correlationId: " + correlationId);
        System.out.println("Pending map instance (Kafka): " + pendingApplicantRequests);

    }
}
