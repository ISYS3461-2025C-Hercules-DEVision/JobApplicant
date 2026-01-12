package com.devision.application.service;

import com.devision.application.dto.ApplicationCreateRequest;
import com.devision.application.dto.ApplicationDTO;
import com.devision.application.dto.CompanyApplicationViewDTO;
import com.devision.application.enums.ApplicationStatus;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository repository;

    private final FileReferenceRepository fileReferenceRepository;

    public List<ApplicationDTO> getApplicationsByApplicantId(String applicantId){
        return repository.findByApplicantId(applicantId)
                .stream()
                .filter(app -> app.getDeletedAt() == null) //exclude soft-delete
                .map(ApplicationMapper::toDto)
                .toList();
    }

    @Override
    public List<ApplicationDTO> getAllApplications() {
        return repository.findAll()
                .stream()
                .filter(app -> app.getDeletedAt() == null)
                .map(ApplicationMapper::toDto)
                .toList();
    }

    @Override
    public void deleteApplication(String applicationId) {
        Application application = repository.findById(applicationId)
                .filter(app -> app.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        application.setDeletedAt(Instant.now());
        application.setUpdatedAt(Instant.now());

        repository.save(application);
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
                req.applicantId(),
                req.jobPostId(),
                req.companyId(),
                (req.documents() == null ? 0 : req.documents().size())
        );

        try {
            // ✅ Defensive checks (helps debugging)
            if (req.applicantId() == null || req.applicantId().isBlank()) {
                log.error("❌ applicantId is NULL/BLANK");
                throw new IllegalArgumentException("applicantId is required");
            }

            if (req.jobPostId() == null || req.jobPostId().isBlank()) {
                log.error("❌ jobPostId is NULL/BLANK");
                throw new IllegalArgumentException("jobPostId is required");
            }

            if (req.companyId() == null || req.companyId().isBlank()) {
                log.error("❌ companyId is NULL/BLANK");
                throw new IllegalArgumentException("companyId is required");
            }

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
                    .build();

            // ✅ Debug: check documents list
            if (application.getDocuments() == null) {
                log.warn("⚠️ application.getDocuments() is NULL. Initializing empty list now.");
                application.setDocuments(new ArrayList<>());
            }

            log.info("✅ Application built: applicationId={}, status={}",
                    application.getApplicationId(),
                    application.getStatus()
            );

            // ✅ Log each document
            if (req.documents() != null && !req.documents().isEmpty()) {
                log.info("📌 Adding {} documents to application", req.documents().size());

                for (int i = 0; i < req.documents().size(); i++) {
                    FileReference reference = req.documents().get(i);

                    log.info("Document[{}] incoming: fileId={}, fileUrl={}, publicId={}, fileType={}",
                            i,
                            reference.getFileId(),
                            reference.getFileUrl(),
                            reference.getPublicId(),
                            reference.getFileType()
                    );

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

                    log.info("✅ Document[{}] added: fileId={}, url={}", i, doc.getFileId(), doc.getFileUrl());
                }
            } else {
                log.info("ℹ️ No documents provided in request.");
            }

            log.info("💾 Saving application to database...");
            Application saved = repository.save(application);

            log.info("✅ Saved application: applicationId={}, documentsSaved={}",
                    saved.getApplicationId(),
                    (saved.getDocuments() == null ? 0 : saved.getDocuments().size())
            );

            log.info("========== [CREATE APPLICATION] SUCCESS ==========");

            return ApplicationMapper.toDto(saved);

        } catch (Exception ex) {
            log.error("========== [CREATE APPLICATION] FAILED ==========");
            log.error("❌ Error message: {}", ex.getMessage());
            log.error("❌ Full stack trace:", ex); // VERY IMPORTANT

            // Optional: rethrow so controller returns proper error
            throw ex;
        }
    }


    public ApplicationDTO updateStatus(String applicationId, ApplicationStatus newStatus){
        Application application = repository.findById(applicationId)
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
                .map(app -> new CompanyApplicationViewDTO(
                        app.getApplicationId(),
                        app.getApplicantId(),
                        pickTimeApplied(app),
                        extractFileUrls(app.getDocuments())
                ))
                .toList();
    }

    @Override
    public void updateApplicationStatus(String jobPostId, String newStatus,String feedback) {
        Application application = repository.findByJobPostId(jobPostId);
        application.setStatus(ApplicationStatus.valueOf(newStatus));
        application.setFeedback(feedback);
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
