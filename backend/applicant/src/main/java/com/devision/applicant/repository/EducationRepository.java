package com.devision.applicant.repository;

import com.devision.applicant.model.Education;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EducationRepository extends MongoRepository<Education, String> {

    void deleteByApplicantId(String applicantId);
}
