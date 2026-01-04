package com.devision.application.service;

import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.command.UploadCoverLetterCommand;
import com.devision.application.dto.internal.command.UploadCvCommand;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import com.devision.application.enums.ApplicationStatus;
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

import java.time.Instant;
import java.util.List;

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
        require(cmd.applicantId(), "applicantId");
        require(cmd.jobPostId(), "jobPostId");
        require(cmd.companyId(), "companyId");

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
                .status(ApplicationStatus.SUBMITTED)
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
        require(cmd.applicantId(), "applicantId");
        require(cmd.applicationId(), "applicationId");
        requireFile(cmd.file(), "file");

        Application app = getByIdOrThrow(cmd.applicationId());
        enforceOwnership(cmd.applicantId(), app);

        FileReference ref = uploadToCloudinary(cmd.file(), "applications/" + app.getApplicationId() + "/cv");
        app.setApplicantCV(ref);
        app.setUpdatedAt(Instant.now());

        Application saved = repo.save(app);

        eventProducer.publish(topic, ApplicationEvent.cvUploaded(saved));
        return toView(saved);
    }

    @Override
    public ApplicationView uploadCoverLetter(UploadCoverLetterCommand cmd) {
        require(cmd.applicantId(), "applicantId");
        require(cmd.applicationId(), "applicationId");
        requireFile(cmd.file(), "file");

        Application app = getByIdOrThrow(cmd.applicationId());
        enforceOwnership(cmd.applicantId(), app);

        FileReference ref = uploadToCloudinary(cmd.file(), "applications/" + app.getApplicationId() + "/cover-letter");
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

    // -------- mapping (internal view) --------
    private ApplicationView toView(Application a) {
        ApplicationView v = new ApplicationView();
        v.applicationId = a.getApplicationId();
        v.applicantId = a.getApplicantId();
        v.jobPostId = a.getJobPostId();
        v.companyId = a.getCompanyId();
        v.status = a.getStatus();
        v.createdAt = a.getCreatedAt();
        v.updatedAt = a.getUpdatedAt();

        if (a.getApplicantCV() != null) v.applicantCV = toFileView(a.getApplicantCV());
        if (a.getCoverLetter() != null) v.coverLetter = toFileView(a.getCoverLetter());
        return v;
    }

    private ApplicationSummaryView toSummaryView(Application a) {
        ApplicationSummaryView v = new ApplicationSummaryView();
        v.applicationId = a.getApplicationId();
        v.jobPostId = a.getJobPostId();
        v.companyId = a.getCompanyId();
        v.status = a.getStatus();
        v.createdAt = a.getCreatedAt();
        return v;
    }

    private ApplicationView.FileView toFileView(FileReference f) {
        ApplicationView.FileView fv = new ApplicationView.FileView();
        fv.fileId = f.getFileId();
        fv.fileUrl = f.getFileUrl();
        fv.publicId = f.getPublicId();     
        fv.fileType = f.getFileType();
        fv.createdAt = f.getCreatedAt();
        fv.updatedAt = f.getUpdatedAt();
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
}
