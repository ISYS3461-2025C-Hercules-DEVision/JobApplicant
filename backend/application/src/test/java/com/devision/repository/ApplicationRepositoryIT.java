package com.devision.application.repository;

import com.devision.application.enums.ApplicationStatus;
import com.devision.application.model.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationRepositoryIT {

    @Autowired ApplicationRepository repo;

    @Test
    void queryMethods_shouldWork() {
        repo.deleteAll();

        String applicantId = "applicant-1";
        String jobPostId = "job-1";
        String companyId = "company-1";

        Application a = new Application();
        a.setApplicationId(UUID.randomUUID().toString());
        a.setApplicantId(applicantId);
        a.setJobPostId(jobPostId);
        a.setCompanyId(companyId);
        a.setStatus(ApplicationStatus.PENDING);
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());

        repo.save(a);

        assertThat(repo.findByApplicantIdOrderByCreatedAtDesc(applicantId)).hasSize(1);
        assertThat(repo.findByJobPostIdOrderByCreatedAtDesc(jobPostId)).hasSize(1);
        assertThat(repo.findByCompanyIdOrderByCreatedAtDesc(companyId)).hasSize(1);
        assertThat(repo.existsByApplicantIdAndJobPostId(applicantId, jobPostId)).isTrue();
    }
}
