package com.devision.application.mock;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.model.Application;
import com.devision.application.enums.FileType;
import com.devision.application.model.FileReference;
import com.devision.application.repository.ApplicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("dev") // ⚠️ chỉ chạy ở dev
public class ApplicationMockDataLoader implements CommandLineRunner {

    private final ApplicationRepository repository;

    public ApplicationMockDataLoader(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return; // tránh seed trùng
        }

        Instant now = Instant.now();

        Application app1 = Application.builder()
                .applicationId(UUID.randomUUID().toString())
                .applicantId(MockIds.APPLICANT_1)
                .jobPostId(MockIds.JOB_1)
                .companyId(MockIds.COMPANY_1)
                .status(ApplicationStatus.SUBMITTED)
                .createdAt(now.minusSeconds(86400))
                .updatedAt(now.minusSeconds(86400))
                .applicantCV(fakePdf("cv"))
                .coverLetter(fakeDocx("cover-letter"))
                .build();

        Application app2 = Application.builder()
                .applicationId(UUID.randomUUID().toString())
                .applicantId(MockIds.APPLICANT_1)
                .jobPostId(MockIds.JOB_2)
                .companyId(MockIds.COMPANY_1)
                .status(ApplicationStatus.SUBMITTED)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(3600))
                .build();

        Application app3 = Application.builder()
                .applicationId(UUID.randomUUID().toString())
                .applicantId(MockIds.APPLICANT_2)
                .jobPostId(MockIds.JOB_3)
                .companyId(MockIds.COMPANY_2)
                .status(ApplicationStatus.SUBMITTED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        repository.save(app1);
        repository.save(app2);
        repository.save(app3);

        System.out.println("✅ Mock applications seeded");
    }

    private FileReference fakePdf(String namePrefix) {
            Instant now = Instant.now();
            return FileReference.builder()
                    .fileId(java.util.UUID.randomUUID().toString())
                    .fileUrl("https://cdn.fake.local/" + namePrefix + "-" + now.toEpochMilli() + ".pdf")
                    .publicId("fake/" + namePrefix + "/pdf-" + now.toEpochMilli())
                    .fileType(FileType.PDF)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        }

        private FileReference fakeDocx(String namePrefix) {
            Instant now = Instant.now();
            return FileReference.builder()
                    .fileId(java.util.UUID.randomUUID().toString())
                    .fileUrl("https://cdn.fake.local/" + namePrefix + "-" + now.toEpochMilli() + ".docx")
                    .publicId("fake/" + namePrefix + "/docx-" + now.toEpochMilli())
                    .fileType(FileType.DOCX)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        }
}
