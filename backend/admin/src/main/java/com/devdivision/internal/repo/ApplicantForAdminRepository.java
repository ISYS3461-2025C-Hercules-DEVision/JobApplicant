package com.devdivision.internal.repo;


import com.devdivision.internal.entity.ApplicantForAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApplicantForAdminRepository extends MongoRepository<ApplicantForAdmin, String> {
    Optional<ApplicantForAdmin> findByEmail(String email);
}
