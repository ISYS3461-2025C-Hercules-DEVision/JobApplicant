package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import com.devision.authentication.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicantConsumer {

    private final ObjectMapper mapper;
    private final UserService userService;

    public ApplicantConsumer(ObjectMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_TOPIC_RESPONSE,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleApplicantApiResponse(String record) throws Exception {
        System.out.println("Received response from APPLICANT: " + record);

        AutheticationApplicantCodeWithUuid payload =
                mapper.readValue(record, AutheticationApplicantCodeWithUuid.class);

        String correlationId = payload.getCorrelationId();
        String applicantId = payload.getApplicantId();

        // attach applicantId to User in Mongo
        userService.attachApplicantToUser(correlationId, applicantId);
    }
}
