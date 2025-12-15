package com.devision.applicant.service;

import com.devision.applicant.dto.education.EducationRequestDto;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Education;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final EducationRepository educationRepository;
    private final KafkaGenericProducer<Object> kafkaProducer;

    private final Map<String, CompletableFuture<String>> pendingEducationUpdateRequests = new ConcurrentHashMap<>();

    @Autowired
    ApplicantService(ApplicantRepository applicantRepository,
                     EducationRepository educationRepository,
                     KafkaGenericProducer<Object> kafkaProducer){
        this.applicantRepository = applicantRepository;
        this.educationRepository = educationRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public CompletableFuture<String> getPendingRequestByCorrelationId(String correlationId){
        return pendingEducationUpdateRequests.get(correlationId);
    }

    //UPDATE EDUCATION FOR PROFILE
    public String updateEducation(String applicantId, List<Education> educations) {

        if(applicantRepository.findById(applicantId).isEmpty()){
            throw new RuntimeException("Applicant not found: " + applicantId);
        }
        String correlationId = UUID.randomUUID().toString();

        EducationRequestDto request = new EducationRequestDto("applicant-education-update-request", applicantId, educations);

        //Send to Kafka (no waiting for response)
        kafkaProducer.sendMessage("applicant-education-update-request", applicantId, request);

        return correlationId;
    }

    //GET applicant by ID
    public Optional<Applicant> getApplicantById(String applicantId){
        return applicantRepository.findById(applicantId);
    }

    //GET all applicant profile
    public List<Applicant> getAllApplicants(){
        List<Applicant> applicants = applicantRepository.findAll();
        return applicants;
    }
}
