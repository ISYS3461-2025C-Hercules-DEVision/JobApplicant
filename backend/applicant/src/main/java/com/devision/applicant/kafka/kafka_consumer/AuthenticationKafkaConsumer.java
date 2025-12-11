package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.dto.AutheticationApplicantCodeWithUuid;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.service.ApplicantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class AuthenticationKafkaConsumer {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaGenericProducer  kafkaGenericProducer;
    @Autowired
    private ApplicantService applicantService;

    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_TOPIC,
            groupId = KafkaConstant.APPLICANT_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consume(String message) throws JsonProcessingException {
        System.out.println("Received message: " + message);
        AutheticationApplicantCodeWithUuid authApplicant = objectMapper.readValue(message, AutheticationApplicantCodeWithUuid.class);

       // logic to get the POST method of Applicant Service and send message

        //kafkaGenericProducer.sendMessage(KafkaConstant.AUTHENTICATION_TOPIC_RESPONSE);
    }


    }
