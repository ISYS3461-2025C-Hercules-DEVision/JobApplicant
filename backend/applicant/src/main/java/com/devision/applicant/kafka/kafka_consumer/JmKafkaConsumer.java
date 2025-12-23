package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantDescDto;
import com.devision.applicant.connection.JmToApplicantCodeWithUuid;
import com.devision.applicant.dto.common.common.DtoWithProcessId;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.service.ApplicantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class JmKafkaConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaGenericProducer<DtoWithProcessId>kafkaGenericProducer;

    @Autowired
    private ApplicantServiceImpl applicantService;

    @KafkaListener(
            topics = KafkaConstant.APPLICATION_TOPIC,
            groupId = KafkaConstant.APPLICANT_GROUP_ID,
            containerFactory = "JmKafkaListenerContainerFactory"
    )
    public void consume(String message){
        //Process the received message
        try{
            System.out.println("Receive message: " + message);
            JmToApplicantCodeWithUuid jm = objectMapper.readValue(message, JmToApplicantCodeWithUuid.class);

            System.out.println(jm.getJmCode());

            //Get the applicant country and skills
            ApplicantDescDto applicantDescDto;
        }

    }
}
