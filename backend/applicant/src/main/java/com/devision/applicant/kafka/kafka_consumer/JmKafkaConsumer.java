package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantToJmDescDto;
import com.devision.applicant.connection.JmToApplicantCodeWithUuid;
import com.devision.applicant.dto.ProfileUpdateResponseEvent;
import com.devision.applicant.dto.common.common.DtoWithProcessId;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.service.ApplicantServiceImpl;
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
    public void consume(String message) throws Exception{
        //Process the received message
        System.out.println("Received message from JM: " + message);

        ProfileUpdateResponseEvent responseEvent = mapper.readValue(message, ProfileUpdateResponseEvent.class);

        pendingJmRequest.complete(responseEvent.correlationId(), responseEvent);
    }
}
