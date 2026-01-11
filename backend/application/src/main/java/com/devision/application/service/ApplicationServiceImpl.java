package com.devision.application.service;

import com.devision.application.config.KafkaConstant;
import com.devision.application.connection.ApplicationCompanyCodeWithUuid;
import com.devision.application.connection.ApplicationToCompanyEvent;
import com.devision.application.connection.PendingCompanyRequest;
import com.devision.application.dto.ApplicationCreateRequest;
import com.devision.application.dto.ApplicationDTO;
import com.devision.application.dto.AppliedApplicationDTO;
import com.devision.application.dto.CompanyApplicationViewDTO;
import com.devision.application.enums.ApplicationStatus;
import com.devision.application.kafka.kafkaProducer.KafkaGenericProducer;
import com.devision.application.mapper.ApplicationMapper;
import com.devision.application.model.Application;
import com.devision.application.model.FileReference;
import com.devision.application.repository.ApplicationRepository;
import com.devision.application.repository.FileReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository repository;

    private final FileReferenceRepository fileReferenceRepository;
    private final PendingCompanyRequest pendingCompanyRequest;
    private final KafkaGenericProducer<ApplicationToCompanyEvent> produce;

    public ApplicationServiceImpl(ApplicationRepository repository, FileReferenceRepository fileReferenceRepository, PendingCompanyRequest pendingCompanyRequest, KafkaGenericProducer<ApplicationToCompanyEvent> produce) {
        this.repository = repository;
        this.fileReferenceRepository = fileReferenceRepository;
        this.pendingCompanyRequest = pendingCompanyRequest;
        this.produce = produce;
    }

    public List<ApplicationDTO> getApplicationsByApplicantId(String applicantId){
        return repository.findByApplicantId(applicantId)
                .stream()
                .filter(app -> app.getDeletedAt() == null) //exclude soft-delete
                .map(ApplicationMapper::toDto)
                .toList();
    }

    public ApplicationDTO getById(String applicationId){
        Application application = repository.findById(applicationId)
                .filter(app -> app.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        return ApplicationMapper.toDto(application);
    }

    public ApplicationDTO createApplication(ApplicationCreateRequest req) {
        log.info("========== [CREATE APPLICATION] START ==========");
        log.info("Incoming request: applicantId={}, jobPostId={}, companyId={}, documentsCount={}",
                req.applicantId(), req.jobPostId(), req.companyId(),
                (req.documents() == null ? 0 : req.documents().size())
        );

        try {
            if (req.applicantId() == null || req.applicantId().isBlank())
                throw new IllegalArgumentException("applicantId is required");
            if (req.jobPostId() == null || req.jobPostId().isBlank())
                throw new IllegalArgumentException("jobPostId is required");
            if (req.companyId() == null || req.companyId().isBlank())
                throw new IllegalArgumentException("companyId is required");

            Application application = Application.builder()
                    .applicationId(UUID.randomUUID().toString())
                    .applicantId(req.applicantId())
                    .jobPostId(req.jobPostId())
                    .companyId(req.companyId())
                    .status(ApplicationStatus.PENDING)
                    .submissionDate(Instant.now())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .isArchived(false)
                    .documents(new ArrayList<>())
                    .build();

            if (req.documents() != null && !req.documents().isEmpty()) {
                for (FileReference reference : req.documents()) {
                    FileReference doc = FileReference.builder()
                            .fileId(reference.getFileId() != null ? reference.getFileId() : UUID.randomUUID().toString())
                            .applicationId(application.getApplicationId())
                            .fileUrl(reference.getFileUrl())
                            .publicId(reference.getPublicId())
                            .fileType(reference.getFileType())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();

                    fileReferenceRepository.save(doc);
                    application.getDocuments().add(doc);
                }
            }


            log.info("Saving application to database...");
            Application saved = repository.save(application);

            log.info("Saved application: applicationId={}, documentsSaved={}",
                    saved.getApplicationId(),
                    (saved.getDocuments() == null ? 0 : saved.getDocuments().size())
            );


            String correlationId = UUID.randomUUID().toString();
            CompletableFuture<ApplicationCompanyCodeWithUuid> future = pendingCompanyRequest.create(correlationId);

            List<String> fileUrls = saved.getDocuments() == null
                    ? List.of()
                    : saved.getDocuments().stream()
                    .map(FileReference::getFileUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .toList();

            ApplicationToCompanyEvent event = new ApplicationToCompanyEvent(
                    correlationId,
                    saved.getApplicationId(),
                    saved.getApplicantId(),
                    fileUrls
            );

            try {
                produce.sendMessage(KafkaConstant.APPLICATION_COMPANY_TOPIC, event);


                ApplicationCompanyCodeWithUuid resp = future.get(5, TimeUnit.SECONDS);
                log.info("Company response received. correlationId={}, resp={}", correlationId, resp);

                // implement in the future

            } catch (Exception e) {
                log.error("Kafka publish / company response failed. correlationId={}", correlationId, e);
            }

            log.info("========== [CREATE APPLICATION] SUCCESS ==========");
            return ApplicationMapper.toDto(saved);

        } catch (Exception ex) {
            log.error("========== [CREATE APPLICATION] FAILED ==========");
            log.error("Error message: {}", ex.getMessage());
            log.error("Full stack trace:", ex);
            throw ex;
        }
    }


    public ApplicationDTO updateStatus(String applicationId, ApplicationStatus newStatus){
        Application application = repository.findById(applicationId)
                .filter(app -> app.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cant find application"));

        application.setStatus(newStatus);
        application.setUpdatedAt(Instant.now());

        Application saved = repository.save(application);
        return ApplicationMapper.toDto(saved);
    }

    @Override
    public List<CompanyApplicationViewDTO> getApplicationsForJobPost(String companyId, String jobPostId) {

        List<Application> applications =
                repository.findByCompanyIdAndJobPostIdOrderBySubmissionDateDesc(companyId, jobPostId);

        return applications.stream()
                .filter(app -> app.getDeletedAt() == null)
                .map(app -> new CompanyApplicationViewDTO(
                        app.getApplicationId(),
                        app.getApplicantId(),
                        pickTimeApplied(app),
                        extractFileUrls(app.getDocuments())
                ))
                .toList();
    }

    @Override
    public void updateApplicationStatus(String jobPostId, String newStatus,String feedback,String applicationId) {
        Application application = repository.findByJobPostIdAndApplicationId(jobPostId,applicationId);
        if (application == null || application.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        application.setStatus(ApplicationStatus.valueOf(newStatus));
        application.setFeedback(feedback);
    }

    @Override
    public List<AppliedApplicationDTO> appliedApplications(String jobPostId) {
        if (jobPostId == null || jobPostId.isBlank()) {
            throw new IllegalArgumentException("jobPostId is required");
        }

        List<Application> apps = repository.findByJobPostId(jobPostId);

        return repository.findByJobPostId(jobPostId).stream()
                .filter(app -> app.getDeletedAt() == null)
                .map(app -> new AppliedApplicationDTO(
                        app.getApplicationId(),
                        app.getApplicantId(),
                        app.getDocuments() == null
                                ? List.of()
                                : app.getDocuments().stream()
                                .map(FileReference::getFileUrl)
                                .filter(url -> url != null && !url.isBlank())
                                .toList()
                ))
                .toList();
    }

    @Override
    public void deleteApplication(String applicationId) {
        if (applicationId == null || applicationId.isBlank()) {
            throw new IllegalArgumentException("applicationId is required");
        }

        Application app = repository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // already deleted? (idempotent)
        if (Boolean.TRUE.equals(app.getIsArchived()) && app.getDeletedAt() != null) {
            return;
        }

        app.setIsArchived(true);
        app.setDeletedAt(Instant.now());
        app.setUpdatedAt(Instant.now());

        repository.save(app);
    }


    private Instant pickTimeApplied(Application app) {
        if (app.getSubmissionDate() != null) return app.getSubmissionDate();
        if (app.getCreatedAt() != null) return app.getCreatedAt();
        return null;
    }

    private List<String> extractFileUrls(List<FileReference> docs) {
        if (docs == null || docs.isEmpty()) return Collections.emptyList();

        return docs.stream()
                .map(FileReference::getFileUrl)
                .filter(url -> url != null && !url.isBlank())
                .toList();
    }

}
