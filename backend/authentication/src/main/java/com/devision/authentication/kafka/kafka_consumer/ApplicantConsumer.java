package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicantConsumer {

    private final ObjectMapper mapper;
    private final UserService userService;
    private PendingApplicantRequests pendingApplicantRequests;
    public ApplicantConsumer(ObjectMapper mapper, UserService userService, PendingApplicantRequests pendingApplicantRequests) {
        this.pendingApplicantRequests = pendingApplicantRequests;
        this.mapper = mapper;
        this.userService = userService;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC_RESPONSE,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleApplicantApiResponse(String record) throws Exception {

        System.out.println("Received response from Admin: " + record);

        AutheticationApplicantCodeWithUuid payload =
                mapper.readValue(record, AutheticationApplicantCodeWithUuid.class);

        String correlationId = payload.getCorrelationId();

        pendingApplicantRequests.complete(correlationId, payload);

        System.out.println("Completed pending request for correlationId: " + correlationId);
        System.out.println("Pending map instance (Kafka): " + pendingApplicantRequests);

    }
}
