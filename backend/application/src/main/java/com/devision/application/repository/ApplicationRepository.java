package com.devision.application.repository;

import com.devision.application.model.Application;
import com.devision.application.enums.ApplicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {

     /**
     * Applicant: xem danh sách application của chính mình
     * Used by: GET /api/v1/applications/me
     */
    List<Application> findByApplicantId(String applicantId);

    /**
     * Partner / Job Manager: xem application theo job post
     * Used by: GET /api/v1/partner/applications/by-job/{jobPostId}
     */
    List<Application> findByJobPostId(String jobPostId);

    /**
     * Partner / Job Manager: xem tất cả application của company
     * Used by: GET /api/v1/partner/applications/by-company/{companyId}
     */
    List<Application> findByCompanyId(String companyId);

    /**
     * Optional: chặn applicant apply cùng 1 job nhiều lần
     * Used by: create application flow
     */
    boolean existsByApplicantIdAndJobPostId(String applicantId, String jobPostId);
}
