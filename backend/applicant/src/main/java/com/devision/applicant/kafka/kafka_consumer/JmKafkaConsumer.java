package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantToJmCodeWithUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JmKafkaConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PendingJmRequest pendingJmRequest;

    @KafkaListener(
            topics = KafkaConstant.PROFILE_UPDATE_RESPONSE,
            groupId = KafkaConstant.APPLICANT_GROUP_ID,
            containerFactory = "jmKafkaListenerContainerFactory"
    )
    public void consume(String message) throws Exception {
        //Process the received message
        System.out.println("Received response from JM: " + message);

        ApplicantToJmCodeWithUuid payload =
                mapper.readValue(message, ApplicantToJmCodeWithUuid.class);

        String correlationId = payload.getCorrelationId();

        pendingJmRequest.complete(correlationId, payload);

        System.out.println("Completed pending request for correlationID: " + correlationId);
        System.out.println("Pending map instance (Kafka): " + pendingJmRequest);
    }
}
