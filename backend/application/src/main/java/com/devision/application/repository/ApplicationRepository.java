package com.devision.application.repository;

import com.devision.application.model.Application;
import com.devision.application.enums.ApplicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByApplicantId(String applicantId);

    List<Application> findByApplicantIdAndStatus(String applicantId, ApplicationStatus status);

    List<Application> findByCompanyIdAndStatus(String companyId, ApplicationStatus status);

    List<Application> findByCompanyIdAndJobPostIdOrderBySubmissionDateDesc(String companyId, String jobPostId);
}
