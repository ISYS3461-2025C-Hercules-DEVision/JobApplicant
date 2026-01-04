package com.devdivision.internal.repo;

import com.devdivision.internal.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByAdminEmail(String adminEmail);

    boolean existsByAdminEmail(String adminEmail);
}
