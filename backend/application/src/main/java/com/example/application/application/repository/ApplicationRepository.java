package com.example.application.application.repository;





import com.example.application.application.enums.ApplicationStatus;
import com.example.application.application.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByApplicantId(String applicantId);

    List<Application> findByApplicantIdAndStatus(String applicantId, ApplicationStatus status);

    List<Application> findByCompanyIdAndStatus(String companyId, ApplicationStatus status);
}
