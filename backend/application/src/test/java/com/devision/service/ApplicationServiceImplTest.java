package com.devision.application.service;

import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.command.UploadCoverLetterCommand;
import com.devision.application.dto.internal.command.UploadCvCommand;
import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;
import com.devision.application.kafka.event.ApplicationEventType;
import com.devision.application.kafka.producer.ApplicationEventProducer;
import com.devision.application.model.Application;
import com.devision.application.model.FileReference;
import com.devision.application.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceImplTest {

    @Mock ApplicationRepository repo;
    @Mock FileStorageService storage;
    @Mock ApplicationEventProducer producer;

    @InjectMocks ApplicationServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        // set topic manually because @Value not injected in unit test
        service.applicationEventsTopic = "ja.application.events";
    }

    @Test
    void create_shouldSaveAndPublishEvent() {
        var cmd = new CreateApplicationCommand("applicant-1", "job-1", "company-1");

        Application saved = new Application();
        saved.setApplicationId("app-1");
        saved.setApplicantId("applicant-1");
        saved.setJobPostId("job-1");
        saved.setCompanyId("company-1");
        saved.setStatus(ApplicationStatus.PENDING);
        saved.setCreatedAt(Instant.now());
        saved.setUpdatedAt(Instant.now());

        when(repo.save(any(Application.class))).thenReturn(saved);

        var view = service.create(cmd);

        assertThat(view.applicationId).isEqualTo("app-1");
        verify(repo).save(any(Application.class));
        verify(producer).publish(eq("ja.application.events"), argThat(e -> e.type() == ApplicationEventType.APPLICATION_CREATED));
    }

    @Test
    void listByApplicant_shouldMapToSummary() {
        Application a = new Application();
        a.setApplicationId("app-1");
        a.setApplicantId("applicant-1");
        a.setJobPostId("job-1");
        a.setCompanyId("company-1");
        a.setStatus(ApplicationStatus.PENDING);
        a.setCreatedAt(Instant.now());

        when(repo.findByApplicantIdOrderByCreatedAtDesc("applicant-1"))
                .thenReturn(List.of(a));

        var list = service.listByApplicant("applicant-1");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).applicationId).isEqualTo("app-1");
    }

    @Test
    void getOwnedByApplicant_wrongOwner_shouldThrow403() {
        Application a = new Application();
        a.setApplicationId("app-1");
        a.setApplicantId("someone-else");

        when(repo.findById("app-1")).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.getOwnedByApplicant("applicant-1", "app-1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void uploadCv_shouldStoreFileAndPublishEvent() {
        Application a = new Application();
        a.setApplicationId("app-1");
        a.setApplicantId("applicant-1");
        a.setJobPostId("job-1");
        a.setCompanyId("company-1");
        when(repo.findById("app-1")).thenReturn(Optional.of(a));
        when(repo.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        var file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "fake".getBytes());

        when(storage.upload(eq(file), anyString()))
                .thenReturn(new FileStorageService.StoredFile(
                        "cloud-public-id",
                        "https://cdn.example.com/cv.pdf",
                        "cv.pdf",
                        "application/pdf",
                        4
                ));

        var view = service.uploadCv(new UploadCvCommand("applicant-1", "app-1", file));

        assertThat(view.applicantCV).isNotNull();
        verify(producer).publish(eq("ja.application.events"), argThat(e -> e.type() == ApplicationEventType.CV_UPLOADED));
    }

    @Test
    void uploadCoverLetter_wrongOwner_shouldThrow403() {
        Application a = new Application();
        a.setApplicationId("app-1");
        a.setApplicantId("someone-else");
        when(repo.findById("app-1")).thenReturn(Optional.of(a));

        var file = new MockMultipartFile("file", "cover.pdf", "application/pdf", "fake".getBytes());

        assertThatThrownBy(() -> service.uploadCoverLetter(new UploadCoverLetterCommand("applicant-1", "app-1", file)))
                .isInstanceOf(AccessDeniedException.class);
    }
}
