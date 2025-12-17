package com.devision.applicant.repository;


import com.devision.applicant.model.Applicant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends MongoRepository<Applicant, String> {
    List<Applicant> findByDeletedAtIsNull();
    
    Optional<Applicant> findByEmail(String email);

    boolean existsByEmail(String email);
    
}
