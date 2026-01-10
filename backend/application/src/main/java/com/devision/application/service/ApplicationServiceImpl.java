package com.devision.application.service;

import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.command.UploadCoverLetterCommand;
import com.devision.application.dto.internal.command.UploadCvCommand;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;
import com.devision.application.model.Application;
import com.devision.application.model.FileReference;
import com.devision.application.repository.ApplicationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.devision.application.kafka.event.ApplicationEvent;
import com.devision.application.kafka.event.ApplicationEventType;
import com.devision.application.kafka.producer.ApplicationEventProducer;
import org.springframework.beans.factory.annotation.Value;
import com.devision.application.api.internal.FileStorageService;
import com.devision.application.api.internal.ApplicationService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repo;
    private final FileStorageService storage;
    private final ApplicationEventProducer eventProducer;

    @Value("${app.kafka.topic.application-events}")
    private String topic;

    public ApplicationServiceImpl(ApplicationRepository repo, FileStorageService storage, ApplicationEventProducer eventProducer) {
        this.repo = repo;
        this.storage = storage;
        this.eventProducer = eventProducer;
    }

    @Override
    public ApplicationView create(CreateApplicationCommand cmd) {
        require(cmd.getApplicantId(), "applicantId");
        require(cmd.getJobPostId(), "jobPostId");
        require(cmd.getCompanyId(), "companyId");

        // Optional anti-duplicate:
        // if (repo.existsByApplicantIdAndJobPostId(cmd.applicantId(), cmd.jobPostId())) {
        //     throw new IllegalStateException("Already applied to this job");
        // }

        Instant now = Instant.now();

        Application app = Application.builder()
                .applicationId(UUID.randomUUID().toString())
                .applicantId(cmd.getApplicantId())
                .jobPostId(cmd.getJobPostId())
                .companyId(cmd.getCompanyId())
                .status(ApplicationStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .isArchived(false)
                .build();

        Application saved = repo.save(app);

        eventProducer.publish(topic, ApplicationEvent.created(saved));

        return toView(saved);
    }

    @Override
    public List<ApplicationSummaryView> listByApplicant(String applicantId) {
        require(applicantId, "applicantId");
        return repo.findByApplicantIdOrderByCreatedAtDesc(applicantId)
                .stream().map(this::toSummaryView).toList();
    }

    @Override
    public ApplicationView getOwnedByApplicant(String applicantId, String applicationId) {
        require(applicantId, "applicantId");
        Application app = getByIdOrThrow(applicationId);

        if (!applicantId.equals(app.getApplicantId())) {
            throw new AccessDeniedException("Forbidden");
        }
        return toView(app);
    }

    @Override
    public ApplicationView uploadCv(UploadCvCommand cmd) {
        require(cmd.getApplicantId(), "applicantId");
        require(cmd.getApplicationId(), "applicationId");
        requireFile(cmd.getFile(), "file");

        Application app = getByIdOrThrow(cmd.getApplicationId());
        enforceOwnership(cmd.getApplicantId(), app);

        FileReference ref = uploadToCloudinary(cmd.getFile(), "applications/" + app.getApplicationId() + "/cv");
        app.setApplicantCV(ref);
        app.setUpdatedAt(Instant.now());

        Application saved = repo.save(app);

        eventProducer.publish(topic, ApplicationEvent.cvUploaded(saved));
        return toView(saved);
    }

    @Override
    public ApplicationView uploadCoverLetter(UploadCoverLetterCommand cmd) {
        require(cmd.getApplicantId(), "applicantId");
        require(cmd.getApplicationId(), "applicationId");
        requireFile(cmd.getFile(), "file");

        Application app = getByIdOrThrow(cmd.getApplicationId());
        enforceOwnership(cmd.getApplicantId(), app);

        FileReference ref = uploadToCloudinary(cmd.getFile(), "applications/" + app.getApplicationId() + "/cover-letter");
        app.setCoverLetter(ref);
        app.setUpdatedAt(Instant.now());

        Application saved = repo.save(app);

        eventProducer.publish(topic, ApplicationEvent.cvUploaded(saved));

        return toView(saved);
    }

    @Override
    public List<ApplicationSummaryView> listByJobPost(String jobPostId) {
        require(jobPostId, "jobPostId");
        return repo.findByJobPostIdOrderByCreatedAtDesc(jobPostId)
                .stream().map(this::toSummaryView).toList();
    }

    @Override
    public List<ApplicationSummaryView> listByCompany(String companyId) {
        require(companyId, "companyId");
        return repo.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream().map(this::toSummaryView).toList();
    }

    @Override
    public ApplicationView getById(String applicationId) {
        return toView(getByIdOrThrow(applicationId));
    }

    @Override
    public List<ApplicationSummaryView> listAll() {
        return repo.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toSummaryView)
                .toList();
    }

    // -------- mapping (internal view) --------
    private ApplicationView toView(Application a) {
        ApplicationView v = new ApplicationView();
        v.setApplicationId(a.getApplicationId());
        v.setApplicantId(a.getApplicantId());
        v.setJobPostId(a.getJobPostId());
        v.setCompanyId(a.getCompanyId());
        v.setStatus(a.getStatus());
        v.setCreatedAt(a.getCreatedAt());
        v.setUpdatedAt(a.getUpdatedAt());

        if (a.getApplicantCV() != null) v.setApplicantCV(toFileView(a.getApplicantCV()));
        if (a.getCoverLetter() != null) v.setCoverLetter(toFileView(a.getCoverLetter()));
        return v;
    }

    private ApplicationSummaryView toSummaryView(Application a) {
        ApplicationSummaryView v = new ApplicationSummaryView();
        v.setApplicationId(a.getApplicationId());
        v.setJobPostId(a.getJobPostId());
        v.setCompanyId(a.getCompanyId());
        v.setStatus(a.getStatus());
        v.setCreatedAt(a.getCreatedAt());
        return v;
    }

    private ApplicationView.FileView toFileView(FileReference f) {
        ApplicationView.FileView fv = new ApplicationView.FileView();
        fv.setFileId(f.getFileId());
        fv.setFileUrl(f.getFileUrl());
        fv.setPublicId(f.getPublicId());     
        fv.setFileType(f.getFileType());
        fv.setCreatedAt(f.getCreatedAt());
        fv.setUpdatedAt(f.getUpdatedAt());
        return fv;
    }

    // -------- helpers --------
    private Application getByIdOrThrow(String id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
    }

    private void enforceOwnership(String applicantId, Application app) {
        if (!applicantId.equals(app.getApplicantId())) throw new AccessDeniedException("Forbidden");
    }

    private FileReference uploadToCloudinary(MultipartFile file, String folder) {
        FileStorageService.StoredFile stored = storage.upload(file, folder);
        Instant now = Instant.now();

        return FileReference.builder()
                .fileId(UUID.randomUUID().toString())
                .fileUrl(stored.url())
                .publicId(stored.publicId())
                .fileType(detectFileType(file))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private static void require(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " is required");
    }

    private static void requireFile(MultipartFile f, String field) {
        if (f == null || f.isEmpty()) throw new IllegalArgumentException(field + " is required");
    }

    private FileType detectFileType(String contentType, String originalFilename) {
        String ct = (contentType == null) ? "" : contentType.toLowerCase();
        String fn = (originalFilename == null) ? "" : originalFilename.toLowerCase();

        if (ct.contains("pdf") || fn.endsWith(".pdf")) return FileType.PDF;
        if (ct.contains("msword") || ct.contains("wordprocessingml")
                || fn.endsWith(".doc") || fn.endsWith(".docx")) {
            return FileType.DOCX;
        }
        return FileType.UNKNOWN;
    }
}


