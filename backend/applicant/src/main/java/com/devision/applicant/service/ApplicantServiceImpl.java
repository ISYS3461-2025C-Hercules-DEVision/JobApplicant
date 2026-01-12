package com.devision.applicant.service;

import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.api.ResumeMapper;
import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantToJmEvent;
import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.Resume;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.MediaPortfolioRepository;
import com.devision.applicant.repository.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository repository;
    private final MediaPortfolioRepository mediaPortfolioRepository;
    private final ResumeRepository resumeRepository;
    private final ImageService imageService;

    private final KafkaGenericProducer<Object> kafkaGenericProducer;
    private final ShardMigrationService shardMigrationService;

    public ApplicantServiceImpl(ApplicantRepository repository, MediaPortfolioRepository mediaPortfolioRepository, ImageService mediaService, ObjectMapper mapper, ResumeRepository resumeRepository, KafkaGenericProducer<Object> kafkaGenericProducer, ShardMigrationService shardMigrationService) {
        this.repository = repository;
        this.mediaPortfolioRepository = mediaPortfolioRepository;
        this.imageService = mediaService;
        this.resumeRepository = resumeRepository;
        this.kafkaGenericProducer = kafkaGenericProducer;
        this.shardMigrationService = shardMigrationService;
    }

    @Override
    public ApplicantDTO create(ApplicantCreateRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        Applicant saved = repository.save(ApplicantMapper.toEntity(req));

        Resume resume = Resume.builder()
                .resumeId(UUID.randomUUID().toString())
                .applicantId(saved.getApplicantId())
                .updatedAt(Instant.now())
                .build();

        resumeRepository.save(resume);

        saved.setResumeId(resume.getResumeId());

        repository.save(saved);
        return ApplicantMapper.toDto(saved);
    }

    @Override
    public ApplicantDTO getById(String id) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        return ApplicantMapper.toDto(a);
    }

    @Override
    public List<ApplicantDTO> getAll() {
        return repository.findByDeletedAtIsNull()
                .stream()
                .map(ApplicantMapper::toDto)
                .toList();
    }

    @Override
    public ApplicantDTO update(String id, ApplicantUpdateRequest req) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));


        //Check if new updated email is different and already used by another applicant
        if (req.email() != null && !req.email().equals(a.getEmail())) {
            if (repository.existsByEmail(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        }
        String oldCountry = a.getCountry();

        ApplicantMapper.updateEntity(a, req);

        boolean countryChanged = req.country() != null && !req.country().equals(oldCountry);

        //Publish to Kafka
        if(countryChanged){
            String correlationId = UUID.randomUUID().toString();
            ApplicantToJmEvent event = new ApplicantToJmEvent();
            event.setCorrelationId(correlationId);
            event.setCountry(req.country());

            kafkaGenericProducer.sendMessage(KafkaConstant.PROFILE_UPDATE_TOPIC, event);
            shardMigrationService.migrateApplicant(a, oldCountry, req.country());
        }else {
            repository.save(a);
        }

        return ApplicantMapper.toDto(a);
    }

    @Override
    public void delete(String id) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        a.setDeletedAt(LocalDateTime.now());
        repository.save(a);
    }


    @Override
    public ApplicantDTO uploadProfileImage(String id, UploadAvatarRequest request){
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        try{
            String avatarUrl = imageService.uploadProfileImage(request.file(), id);
            a.setProfileImageUrl(avatarUrl);
            Applicant saved = repository.save(a);
            return ApplicantMapper.toDto(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload avatar");
        }
    }

    @Override
    public MediaPortfolio uploadMediaPortfolio(String applicantId, UploadMediaPortfolioRequest request){
         repository.findById(applicantId)
                 .filter(x -> x.getDeletedAt() == null)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        try{
            return imageService.uploadMediaPortfolio(
                    request.file(),
                    applicantId,
                    request.title(),
                    request.description(),
                    request.visibility() != null ? request.visibility() : Visibility.PRIVATE
            );

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload portfolio");
        }
    }

    @Override
    public List<MediaPortfolio> getMediaPortfolio(String applicantId, Visibility visibility) {
        repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        if (visibility == null) {
            return mediaPortfolioRepository.findByApplicantId(applicantId);
        }
        return mediaPortfolioRepository.findByApplicantIdAndVisibility(applicantId, visibility);
    }

    @Override
    public void deleteMediaPortfolio(String applicantId, String mediaId){
        repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        MediaPortfolio mediaPortfolio = mediaPortfolioRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));

        if(!mediaPortfolio.getApplicantId().equals(applicantId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own portfolio");
        }

        try{
            imageService.deleteMedia(mediaId, applicantId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete media from storage");
        }
    }

    @Override
    public ApplicantDTO deactivateApplicantAccount(String applicantId) {
        Applicant a = repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));
        a.setIsActivated(false);
        Applicant saved = repository.save(a);
        System.out.println(saved.getApplicantId());
        System.out.println(saved.getIsActivated());
        ChangeStatusDto change = new ChangeStatusDto(saved.getApplicantId(), saved.getIsActivated());
        System.out.println(change.id());
        System.out.println(change.status());
        kafkaGenericProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_CHANGE_STATUS_TOPIC,change);
        return ApplicantMapper.toDto(saved);
    }

    @Override
    public ApplicantDTO activateApplicantAccount(String applicantId) {
        Applicant a = repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));
        a.setIsActivated(true);
        Applicant saved = repository.save(a);
        System.out.println(saved.getApplicantId());
        System.out.println(saved.getIsActivated());
        ChangeStatusDto change = new ChangeStatusDto(saved.getApplicantId(), saved.getIsActivated());
        System.out.println(change.id());
        System.out.println(change.status());
        kafkaGenericProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_CHANGE_STATUS_TOPIC,change);
        return ApplicantMapper.toDto(saved);
    }

    @Override
    public ResumeDTO updateResume(String applicantId, ResumeUpdateRequest request){
        Applicant a = repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        Resume resume = resumeRepository.findByApplicantId(applicantId)
                .orElse(null);

        if(resume == null){
            resume = Resume.builder()
                    .resumeId(UUID.randomUUID().toString())
                    .applicantId(applicantId)
                    .build();
            if(a.getResumeId() == null){
                a.setResumeId(resume.getResumeId());
            }
        }

        ResumeMapper.updateEntity(resume, request);
        resume.setUpdatedAt(Instant.now());

        if(!a.getIsResumeUpdated()){
            a.setIsResumeUpdated(true);
            repository.save(a);
        }
        resume = resumeRepository.save(resume);

        return ResumeMapper.toDto(resume);
    }

    @Override
    public ResumeDTO getResume(String applicantId){
        Resume resume = resumeRepository.findByApplicantId(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));
        return ResumeMapper.toDto(resume);
    }

    @Override
    public void deleteResume(String applicantId) {
        Applicant applicant = repository.findById(applicantId)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        String resumeId = applicant.getResumeId();
        if (resumeId == null) {
            log.info("No resume to delete for applicant {}", applicantId);
            return;
        }

        // Delete from Resume collection
        resumeRepository.deleteById(resumeId);   // ← this is the built-in method

        log.info("Resume {} deleted successfully", resumeId);

        // Clear from Applicant collection
        applicant.setResumeId(null);
        repository.save(applicant);

        log.info("Resume reference removed from Applicant {}", applicantId);
    }

    @Override
    public List<ResumeDTO> getAllResumes() {
        return resumeRepository.findAll()
                .stream()
                .map(ResumeMapper::toDto)
                .toList();
    }



}
