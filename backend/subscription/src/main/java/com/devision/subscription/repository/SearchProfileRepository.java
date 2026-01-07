package com.devision.subscription.repository;

import com.devision.subscription.model.SearchProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SearchProfileRepository extends MongoRepository<SearchProfile, String> {
    Optional<SearchProfile> findByApplicantId(String applicantId);
}
