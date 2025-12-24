package com.devision.applicant.service;

import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.MediaPortfolioRepository;
import lombok.extern.slf4j.Slf4j;
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
    private final ImageService imageService;

    private final KafkaGenericProducer<Object> kafkaGenericProducer;

    public ApplicantServiceImpl(ApplicantRepository repository, MediaPortfolioRepository mediaPortfolioRepository, ImageService mediaService, ObjectMapper mapper, KafkaGenericProducer<Object> kafkaGenericProducer) {
        this.repository = repository;
        this.mediaPortfolioRepository = mediaPortfolioRepository;
        this.imageService = mediaService;
        this.kafkaGenericProducer = kafkaGenericProducer;
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

        //Check if new updated email is different and already used by another applicant
        if (req.email() != null && !req.email().equals(a.getEmail())) {
            if (repository.existsByEmail(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        }

        //Flag for checking updating in fields country or skills
        boolean countryChanged = req.country() != null && !req.country().equals(a.getCountry());
        boolean skillsChanged = req.skills() != null && !Objects.equals(req.skills(), a.getSkills());

        ApplicantMapper.updateEntity(a, req);
        Applicant saved = repository.save(a);
        ApplicantDTO dto = ApplicantMapper.toDto(saved);

        //Publish to Kafka when country or skills changed
        if (countryChanged || skillsChanged) {
            String correlationId = UUID.randomUUID().toString();

            ProfileUpdateEvent event = new ProfileUpdateEvent(
                    saved.getApplicantId(),
                    skillsChanged ? "skills" : "country",
                    skillsChanged ? a.getSkills() : a.getCountry(),
                    skillsChanged ? req.skills() : req.country(),
                    Instant.now()
            );

            kafkaGenericProducer.sendMessage(KafkaConstant.PROFILE_UPDATE_TOPIC, event);
            System.out.println("Published profile update to Kafka, correlationId: " + correlationId);

//        return ApplicantMapper.toDto(repository.save(a));
        }
        return dto;
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
    public ApplicantDTO deleteProfileByField(String id, String fieldName){
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        switch (fieldName){
            case "fullName":
                a.setFullName(null);
                break;
            case "email":
                a.setEmail(null);
                break;
            case "country":
                a.setCountry(null);
                break;
            case "city":
                a.setCity(null);
                break;
            case "streetAddress":
                a.setStreetAddress(null);
                break;
            case "phoneNumber":
                a.setPhoneNumber(null);
                break;
            case "objectiveSummary":
                a.setObjectiveSummary(null);
                break;
            case "profileImageUrl":
                a.setProfileImageUrl(null);
                break;
            case "skills":
                a.setSkills(null);
                break;
            case "educations":
                a.setEducations(null);
                break;
            case "experiences":
                a.setExperiences(null);
                break;
            case "mediaPortfolios":
                a.setMediaPortfolios(null);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid field name: " + fieldName);
        }

        //Save the updated
        Applicant saved = repository.save(a);
        return ApplicantMapper.toDto(saved);
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


}
