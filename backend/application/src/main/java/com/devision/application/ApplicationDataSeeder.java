package com.devision.application;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;
import com.devision.application.model.Application;
import com.devision.application.model.FileReference;
import com.devision.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ApplicationDataSeeder implements CommandLineRunner {

    private final ApplicationRepository applicationRepository;

    @Override
    public void run(String... args) {
        if (applicationRepository.count() > 0) {
            System.out.println("Application collection already seeded");
            return;
        }

        String applicationId = UUID.randomUUID().toString();
        String applicantId = UUID.randomUUID().toString();
        String jobPostId = UUID.randomUUID().toString();
        String companyId = UUID.randomUUID().toString();

        FileReference cv = FileReference.builder()
                .fileId(UUID.randomUUID().toString())
                .applicationId(applicationId)               // nếu FileReference có field này
                .fileUrl("https://res.cloudinary.com/demo/cv.pdf")
                .publicId("demo/cv")                        // optional
                .fileType(FileType.PDF)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        FileReference coverLetter = FileReference.builder()
                .fileId(UUID.randomUUID().toString())
                .applicationId(applicationId)               // nếu FileReference có field này
                .fileUrl("https://res.cloudinary.com/demo/cover_letter.pdf")
                .publicId("demo/cover_letter")              // optional
                .fileType(FileType.PDF)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Application application = Application.builder()
                .applicationId(applicationId)
                .applicantId(applicantId)
                .jobPostId(jobPostId)
                .companyId(companyId)
                .status(ApplicationStatus.PENDING)
                .submissionDate(Instant.now())
                .feedback(null)
                .applicantCV(cv)            // ✅ add
                .coverLetter(coverLetter)   // ✅ add
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isArchived(false)
                .deletedAt(null)
                .build();

        applicationRepository.save(application);
        System.out.println("Seeded Application data successfully");
    }
}
