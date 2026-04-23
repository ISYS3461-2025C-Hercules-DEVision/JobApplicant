package com.devision.subscription.repository; // Repository package

import com.devision.subscription.model.SearchProfile; // Search profile entity
import org.springframework.data.mongodb.repository.MongoRepository; // Spring Data Mongo repository

import java.util.Optional; // Optional return type

public interface SearchProfileRepository extends MongoRepository<SearchProfile, String> { // Search profile repository
    Optional<SearchProfile> findByApplicantId(String applicantId); // Lookup by applicant id
} // End repository
