package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthenticationApplicantForAdminCodeWithUuid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicantForAdminConsumer {
    private final ObjectMapper mapper;
    private final PendingApplicantForAdminRequests pending;
    public ApplicantForAdminConsumer(ObjectMapper mapper, PendingApplicantForAdminRequests pending) {
        this.mapper = mapper;
        this.pending = pending;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_APPLICANT_FOR_ADMIN_TOPIC_RESPONSE,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleAdminApiResponse(String record) throws Exception {
        System.out.println("Received response from APPLICANT For Admin: " + record);
        AuthenticationApplicantForAdminCodeWithUuid payload =
                mapper.readValue(record, AuthenticationApplicantForAdminCodeWithUuid.class);
        String correlationId = payload.getCorrelationId();
        pending.complete(correlationId, payload);

        System.out.println("Completed pending request for correlationId: " + correlationId);
        System.out.println("Pending map instance (Kafka): " + pending);

    }
}
