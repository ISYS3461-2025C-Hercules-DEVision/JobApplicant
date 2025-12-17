package com.devision.applicant.service;

import com.devision.applicant.dto.applicant.ApplicantProfileDTO;
import com.devision.applicant.dto.applicant.ApplicantProfileRequest;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.EducationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ApplicantProfileService {
    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;
    private final EducationRepository educationRepository;
    private final KafkaGenericProducer<Object> kafkaProducer;

    private final Map<String, CompletableFuture<String>> pendingEducationUpdateRequests = new ConcurrentHashMap<>();

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    ApplicantProfileService(ApplicantRepository applicantRepository, PasswordEncoder passwordEncoder,
                            EducationRepository educationRepository,
                            KafkaGenericProducer<Object> kafkaProducer){
        this.applicantRepository = applicantRepository;
        this.passwordEncoder = passwordEncoder;
        this.educationRepository = educationRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public CompletableFuture<String> getPendingRequestByCorrelationId(String correlationId){
        return pendingEducationUpdateRequests.get(correlationId);
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

    //UPDATE profile
    public ApplicantProfileDTO updateProfile (String email, ApplicantProfileRequest request){
        Applicant a = applicantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Applicant not found with email: " + email));

        //Email update (uniqueness check)
        if(request.getEmail() != null && !request.getEmail().equals(a.getEmail())){
            if(applicantRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException("Email already exists");
            }
            a.setEmail(request.getEmail());
        }

        //Password update
        if(request.getPassword() != null && !request.getPassword().isBlank()){
            a.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        //Update other fields
        a.setPhoneNumber(request.getPhoneNumber());
        a.setStreetAddress(request.getStreetAddress());
        a.setCity(request.getCity());
        a.setCountry(request.getCountry());
        a.setProfileImageUrl(request.getProfileImageUrl());
        a.setObjectiveSummary(request.getObjectiveSummary());
        a.setUpdatedAt(Instant.now());
        a.setEducations(request.getEducations());
        a.setExperiences(request.getExperiences());

        Applicant updatedApplicant = applicantRepository.save(a);

        //Map to response
        return mapToResponse(updatedApplicant);
    }

    private ApplicantProfileDTO mapToResponse (Applicant applicant){
        ApplicantProfileDTO response = new ApplicantProfileDTO();
        response.setEmail(applicant.getEmail());
        response.setPassword(applicant.getPassword());
        response.setCity(applicant.getCity());
        response.setStreetAddress(applicant.getStreetAddress());
        response.setPhoneNumber(applicant.getPhoneNumber());
        response.setCountry(applicant.getCountry());
        response.setObjectiveSummary(applicant.getObjectiveSummary());
        response.setEducations(applicant.getEducations());
        response.setExperiences(applicant.getExperiences());
        response.setCreatedAt(applicant.getCreatedAt());
        response.setUpdatedAt(applicant.getUpdatedAt());
        return response;
    }

    //CREATE Education, Work Experience and Objective Summary

}
