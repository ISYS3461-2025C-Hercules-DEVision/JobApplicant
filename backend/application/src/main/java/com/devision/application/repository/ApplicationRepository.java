package com.devision.application.repository;

import com.devision.application.model.Application;
import com.devision.application.enums.ApplicationStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByApplicantId(String applicantId);

    List<Application> findByApplicantIdAndStatus(String applicantId, ApplicationStatus status);

    List<Application> findByCompanyIdAndStatus(String companyId, ApplicationStatus status);
    List<Application> findByJobPostId(String jobPostId);


    List<Application> findByCompanyIdAndJobPostIdOrderBySubmissionDateDesc(String companyId, String jobPostId);

    Application findByJobPostIdAndApplicationId(String jobPostId,String applicationId);
    List<Application> findByApplicantIdAndDeletedAtIsNull(String applicantId);

    Optional<Application> findByApplicationIdAndDeletedAtIsNull(String applicationId);

    List<Application> findByCompanyIdAndJobPostIdAndDeletedAtIsNullOrderBySubmissionDateDesc(String companyId, String jobPostId);

    List<Application> findByJobPostIdAndDeletedAtIsNull(String jobPostId);

    Optional<Application> findByJobPostIdAndApplicationIdAndDeletedAtIsNull(String jobPostId, String applicationId);

}
