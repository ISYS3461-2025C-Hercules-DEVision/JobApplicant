package com.devision.authentication.repository;

import com.devision.authentication.model.AuthAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthAccountRepository extends MongoRepository<AuthAccount, String> {

    Optional<AuthAccount> findByEmail(String email);

    Optional<AuthAccount> findByApplicantId(String applicantId);

    Optional<AuthAccount> findBySsoId(String ssoId);

    boolean existsByEmail(String email);

    boolean existsByApplicantId(String applicantId);
}
