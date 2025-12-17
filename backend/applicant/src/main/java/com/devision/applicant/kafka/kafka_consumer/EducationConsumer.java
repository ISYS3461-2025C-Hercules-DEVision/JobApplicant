package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.dto.education.EducationRequestDto;
import com.devision.applicant.kafka.KafkaConstant;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Education;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EducationConsumer {
    private final EducationRepository educationRepository;
    private final ApplicantRepository applicantRepository;

    @Autowired
    public EducationConsumer(EducationRepository educationRepository, ApplicantRepository applicantRepository){
        this.educationRepository = educationRepository;
        this.applicantRepository = applicantRepository;
    }

    @KafkaListener(topics = KafkaConstant.APPLICANT_TOPIC_RESPONSE, groupId = KafkaConstant.APPLICANT_GROUP_ID, containerFactory = "defaultKafkaListenerContainerFactory")
    public void handleEducationUpdateRequest(EducationRequestDto request){
        String correlationId = request.correlationId();
        String applicantId = request.applicantId();
        List<Education> newEducations = request.educations();

        applicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        //Delete old record
        educationRepository.deleteByApplicantId(applicantId);

        //Save new education with proper fields
        Instant now = Instant.now();
        for(Education e : newEducations){
            e.setEducationId(UUID.randomUUID().toString());
            e.setApplicantId(applicantId);
            e.setCreatedAt(now);
            e.setUpdatedAt(now);

            educationRepository.save(e);
        }
    }

}
