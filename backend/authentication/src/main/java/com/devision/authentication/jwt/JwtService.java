package com.devision.authentication.jwt;

import com.devision.authentication.dto.jwtUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.auth.jwt-secret}")
    private String jwtSecret;

    @Value("${app.auth.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.auth.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * LOCAL access token (non-SSO) -> should be revocable via Redis (jti)
     */
    public String generateAccessToken(jwtUserDto user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti) //  JTI for revocation checks
                .setSubject(user.userId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(user.toClaims())
                .claim("type", "access")
                .claim("provider", "LOCAL") //  marks as non-SSO token
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * LOCAL refresh token (also revocable if you want)
     */
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti) // JTI
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", "refresh")
                .claim("provider", "LOCAL") //
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    public boolean isAccessToken(String token) {
        Claims claims = parseClaims(token);
        return "access".equals(claims.get("type", String.class));
    }

    // Convenience helpers you’ll use in logout/revoke logic

    public String getJti(String token) {
        return parseClaims(token).getId();
    }

    public long getExpirationEpochMs(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    public String getProvider(String token) {
        String provider = parseClaims(token).get("provider", String.class);
        return provider != null ? provider : "LOCAL";
    }
}
