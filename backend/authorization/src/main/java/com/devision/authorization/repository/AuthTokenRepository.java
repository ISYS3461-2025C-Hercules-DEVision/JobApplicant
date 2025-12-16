package com.devision.authorization.repository;

import com.devision.authorization.enums.TokenType;
import com.devision.authorization.model.AuthToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AuthTokenRepository extends MongoRepository<AuthToken, String> {

    Optional<AuthToken> findByToken(String token);

    List<AuthToken> findByAuthId(String authId);

    List<AuthToken> findByAuthIdAndTokenType(String authId, TokenType tokenType);

    List<AuthToken> findByAuthIdAndIsRevokedFalse(String authId);

    long deleteByExpiresAtBefore(Instant now); // optional cleanup (TTL already handles this)
}
