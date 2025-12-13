package com.devision.applicant;

import com.devision.applicant.model.*;
import com.devision.applicant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.ProficiencyLevel;
import com.devision.applicant.enums.Visibility;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("dev") // chỉ chạy khi bạn bật profile dev
public class TestDataSeeder implements CommandLineRunner {

    private final ApplicantRepository applicantRepository;
    private final ResumeRepository resumeRepository;
    private final SearchProfileRepository searchProfileRepository;
    private final MediaPortfolioRepository mediaPortfolioRepository;
    private final ApplicantSkillRepository applicantSkillRepository;

    @Override
    public void run(String... args) {
        // tránh seed lặp lại mỗi lần run
        if (applicantRepository.count() > 0) return;

        Instant now = Instant.now();

        String applicantId = uuid();
        String resumeId = uuid();
        String searchProfileId = uuid();

        // ---- Applicant collection ----
        Applicant applicant = Applicant.builder()
                .applicantId(applicantId)
                .fullName("Test Applicant")
                .email("test@applicant.com")
                .country("VN")
                .city("HCMC")
                .streetAddress("123 Test Street")
                .phoneNumber("0900000000")
                .profileImageUrl("https://example.com/profile.jpg")
                .isActivated(true)
                .isArchived(false)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

        applicantRepository.save(applicant);

        // ---- SearchProfile collection ----
        SearchProfile searchProfile = SearchProfile.builder()
                .searchProfileId(searchProfileId)
                .applicantId(applicantId)
                .profileName("Default Search")
                .desiredCountry("Australia")
                .desiredCity("Melbourne")
                .desiredMinSalary(1000.0)
                .desiredMaxSalary(3000.0)
                .jobTitles(List.of("Intern", "Junior Developer"))
                .technicalBackground(List.of("Java", "Spring Boot"))
                .employmentStatus(List.of("Open to work"))
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        searchProfileRepository.save(searchProfile);

        // ---- Resume collection (embedded edu + exp) ----
        Education edu1 = Education.builder()
                .educationId(uuid())
                .applicantId(applicantId)
                .institution("RMIT University")
                .degree("Bachelor")
                .fromYear(2022)
                .toYear(2025)
                .gpa(3.5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        WorkExperience exp1 = WorkExperience.builder()
                .workExpId(uuid())
                .applicantId(applicantId)
                .jobTitle("Intern")
                .companyName("DevDivision")
                .fromYear("2024")
                .toYear("2025")
                .description("Worked on microservices & MongoDB.")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Resume resume = Resume.builder()
                .resumeId(resumeId)
                .applicantId(applicantId)
                .headline("Java Developer")
                .objective("Looking for backend role.")
                .education(List.of(edu1))
                .experience(List.of(exp1))
                .skills(List.of("Java", "Spring Boot", "MongoDB"))
                .certifications(List.of("AWS CCP"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        resumeRepository.save(resume);

        // ---- MediaPortfolio collection ----
        MediaPortfolio media1 = MediaPortfolio.builder()
                .mediaId(uuid())
                .applicantId(applicantId)
                .fileUrl("https://res.cloudinary.com/demo/image/upload/v1/sample.jpg")
                .publicId("sample_public_id")
                .mediaType(MediaType.IMAGE)    
                .title("Portfolio Image")
                .description("Test portfolio item")
                .visibility(Visibility.PUBLIC)
                .createdAt(now)
                .build();

        mediaPortfolioRepository.save(media1);

        // ---- ApplicantSkill collection ----
        ApplicantSkill skill1 = ApplicantSkill.builder()
                .id(uuid())
                .applicantId(applicantId)
                .skillId(uuid())
                .proficiency(ProficiencyLevel.BEGINNER)
                .endorsedBy(List.of(uuid(), uuid()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        applicantSkillRepository.save(skill1);

        System.out.println("✅ Seeded Applicant Service test data:");
        System.out.println("ApplicantId = " + applicantId);
        System.out.println("ResumeId = " + resumeId);
        System.out.println("SearchProfileId = " + searchProfileId);
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
