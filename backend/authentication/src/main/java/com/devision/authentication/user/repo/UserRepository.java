package com.devision.authentication.user.repo;

import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.entity.UserRole;
import com.mongodb.client.MongoIterable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);

    boolean existsByRole(UserRole role);

    Optional<User> findByApplicantId(String applicantId);
}
