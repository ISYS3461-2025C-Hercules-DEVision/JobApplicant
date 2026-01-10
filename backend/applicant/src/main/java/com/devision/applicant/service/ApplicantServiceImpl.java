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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

        Resume r = resumeRepository.findById()
        //Check if new updated email is different and already used by another applicant
        if (req.email() != null && !req.email().equals(a.getEmail())) {
            if (repository.existsByEmail(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        }
        String oldCountry = a.getCountry();
        List<String> oldSkills = a.getSkills();

        ApplicantMapper.updateEntity(a, req);

        boolean countryChanged = req.country() != null && !req.country().equals(oldCountry);
        boolean skillChanged = req.skills() != null && !req.skills().equals(oldSkills);

        //Publish to Kafka
        if(countryChanged || skillChanged){
            String correlationId = UUID.randomUUID().toString();
            ApplicantToJmEvent event = new ApplicantToJmEvent();
            event.setCorrelationId(correlationId);
            event.setCountry(req.country() != null ? req.country() : oldCountry);
            event.setSkills(req.skills() != null ? req.skills() : oldSkills);

            kafkaGenericProducer.sendMessage(KafkaConstant.PROFILE_UPDATE_TOPIC, event);
        }

        if(countryChanged){
            //Trigger shard migration (copy and delete)
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
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

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
        String resumeId = UUID.randomUUID().toString();
        ResumeDTO updatedResume =  new ResumeDTO(
                resumeId,
                a.getApplicantId(),
                request.headline(),
                request.objective(),
                request.education(),
                request.experience(),
                request.skills(),
                request.certifications(),
                request.mediaPortfolios(),
                Instant.now(),
                request.minSalary(),
                request.maxSalary()
        );

        resumeRepository.save(ResumeMapper.toEntity(updatedResume));
        return updatedResume;



    }
}
