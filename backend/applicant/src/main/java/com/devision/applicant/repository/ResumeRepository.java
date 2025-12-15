package com.devision.applicant.repository;

import com.devision.applicant.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResumeRepository extends MongoRepository<Resume, String> {

    Optional<Resume> findByApplicantId(String applicantId);

    boolean existsByApplicantId(String applicantId);

    void deleteByApplicantId(String applicantId);
}
