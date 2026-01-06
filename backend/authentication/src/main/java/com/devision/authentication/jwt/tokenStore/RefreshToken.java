package com.devision.authentication.jwt.tokenStore;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;

    private String userId;

    private String token;      // store raw token (or hashed if you want more security)

    private Date expiryDate;

    private boolean revoked;
}
