package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.dto.HandleChangeStatusReqDto;
import com.devision.authentication.user.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicantChangeStatus {
    private final ObjectMapper mapper;
    private final UserServiceImpl userService;

    public ApplicantChangeStatus(ObjectMapper mapper, UserServiceImpl userService) {
        this.mapper = mapper;
        this.userService = userService;
    }
    @KafkaListener(
            topics = KafkaConstant.AUTHENTICATION_APPLICANT_CHANGE_STATUS_TOPIC,
            groupId = KafkaConstant.AUTHENTICATION_GROUP_ID,
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void handleChangeStatusConsume(String record) throws JsonProcessingException {
        System.out.println("Received response from Applicant: " + record);
        HandleChangeStatusReqDto consume = mapper.readValue(record, HandleChangeStatusReqDto.class);
        System.out.println("Applicant ID: " + consume.id());
        System.out.println("Status change: " + consume.status());
        userService.updateStatus(consume);
    }


}
