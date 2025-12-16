package com.devision.applicant.service;


import com.devision.applicant.entity.Applicant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicantRepository extends MongoRepository<Applicant, String> {
    List<Applicant> findByDeletedAtIsNull();

    boolean existsByEmail(String email);
}
