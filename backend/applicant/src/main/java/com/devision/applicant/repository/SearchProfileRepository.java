package com.devision.applicant.repository;

import com.devision.applicant.model.SearchProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SearchProfileRepository extends MongoRepository<SearchProfile, String> {

    List<SearchProfile> findByApplicantId(String applicantId);

    Optional<SearchProfile> findByApplicantIdAndIsActiveTrue(String applicantId);

    void deleteByApplicantId(String applicantId);
}
