package com.devision.authorization.model;

import com.devision.authorization.enums.TokenType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auth_tokens")
public class AuthToken {

    @Id
    private String tokenId;      // UUID

    @Indexed
    private String authId;       // UUID (FK-like reference to AuthAccount)

    @Indexed(unique = true)
    private String token;        // store token string (or hash if you want safer)

    private TokenType tokenType; // ACCESS | REFRESH

    /**
     * TTL index: Mongo will auto-delete the document when expiresAt < now
     * Note: TTL works on Date/Instant fields.
     */
    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt;

    private boolean isRevoked;

    private Instant createdAt;
    private Instant updatedAt;
}
