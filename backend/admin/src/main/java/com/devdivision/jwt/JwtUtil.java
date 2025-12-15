package com.devdivision.jwt;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

/**
 * Utility class for JWT token verification
 * Can be used by multiple services that need to verify JWT tokens
 */
@Component
public class JwtUtil {

    private JWKSet jwkSet;
    private Instant jwkSetLastFetched;
    private String cachedJwksUrl;
    private static final long JWKS_CACHE_DURATION_SECONDS = 3600; // Cache for 1 hour

    /**
     * Verify and parse a JWT token using JWKS from the provided URL
     * 
     * @param token The JWT token to verify
     * @param jwksUrl The JWKS URL to fetch public keys from
     * @param expectedIssuer The expected issuer claim value
     * @param expectedAudience The expected audience claim value
     * @return JWTClaimsSet containing all claims from the verified token
     * @throws Exception if token is invalid
     */
    public JWTClaimsSet verifyToken(String token, String jwksUrl, String expectedIssuer, String expectedAudience) throws Exception {
        // Parse the JWT
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        // Get the key ID from the token header
        String keyId = signedJWT.getHeader().getKeyID();
        
        // Fetch JWKS if not cached, cache is expired, or URL changed
        if (jwkSet == null || jwkSetLastFetched == null || 
            !jwksUrl.equals(cachedJwksUrl) ||
            Instant.now().isAfter(jwkSetLastFetched.plusSeconds(JWKS_CACHE_DURATION_SECONDS))) {
            fetchJWKS(jwksUrl);
        }
        
        // Get the public key for verification
        ECKey ecKey = (ECKey) jwkSet.getKeyByKeyId(keyId);
        if (ecKey == null) {
            throw new SecurityException("Key with ID " + keyId + " not found in JWKS");
        }
        
        // Verify the signature
        JWSVerifier verifier = new ECDSAVerifier(ecKey.toECPublicKey());
        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("Invalid JWT signature");
        }
        
        // Get claims
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        
        // Verify standard claims
        verifyClaims(claims, expectedIssuer, expectedAudience);
        
        return claims;
    }

    /**
     * Verify and parse a JWT token with multiple possible issuers and audiences
     * Useful for services that accept tokens from multiple sources
     * 
     * @param token The JWT token to verify
     * @param jwksUrl The JWKS URL to fetch public keys from
     * @param expectedIssuers Array of acceptable issuer values
     * @param expectedAudiences Array of acceptable audience values
     * @return JWTClaimsSet containing all claims from the verified token
     * @throws Exception if token is invalid
     */
    public JWTClaimsSet verifyTokenMultipleIssuers(String token, String jwksUrl, String[] expectedIssuers, String[] expectedAudiences) throws Exception {
        // Parse the JWT
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        // Get the key ID from the token header
        String keyId = signedJWT.getHeader().getKeyID();
        
        // Fetch JWKS if not cached, cache is expired, or URL changed
        if (jwkSet == null || jwkSetLastFetched == null || 
            !jwksUrl.equals(cachedJwksUrl) ||
            Instant.now().isAfter(jwkSetLastFetched.plusSeconds(JWKS_CACHE_DURATION_SECONDS))) {
            fetchJWKS(jwksUrl);
        }
        
        // Get the public key for verification
        ECKey ecKey = (ECKey) jwkSet.getKeyByKeyId(keyId);
        if (ecKey == null) {
            throw new SecurityException("Key with ID " + keyId + " not found in JWKS");
        }
        
        // Verify the signature
        JWSVerifier verifier = new ECDSAVerifier(ecKey.toECPublicKey());
        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("Invalid JWT signature");
        }
        
        // Get claims
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        
        // Verify standard claims with multiple issuers/audiences
        verifyClaimsMultiple(claims, expectedIssuers, expectedAudiences);
        
        return claims;
    }

    /**
     * Extract a claim from JWT claims set
     * 
     * @param claims The claims set
     * @param claimName The name of the claim to extract
     * @return The claim value, or null if not found
     */
    public Object getClaim(JWTClaimsSet claims, String claimName) {
        try {
            return claims.getClaim(claimName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if a token has expired
     * 
     * @param claims The JWT claims set
     * @return true if expired, false otherwise
     */
    public boolean isExpired(JWTClaimsSet claims) {
        Date expirationTime = claims.getExpirationTime();
        return expirationTime == null || expirationTime.before(new Date());
    }

    /**
     * Fetch JWKS from the provided URL
     */
    private void fetchJWKS(String jwksUrl) throws Exception {
        jwkSet = JWKSet.load(URI.create(jwksUrl).toURL());
        jwkSetLastFetched = Instant.now();
        cachedJwksUrl = jwksUrl;
    }

    /**
     * Verify JWT standard claims (issuer, audience, expiration)
     */
    private void verifyClaims(JWTClaimsSet claims, String expectedIssuer, String expectedAudience) throws Exception {
        // Verify issuer
        String issuer = claims.getIssuer();
        if (!expectedIssuer.equals(issuer)) {
            throw new SecurityException("Invalid issuer: " + issuer);
        }
        
        // Verify audience
        String audience = claims.getAudience().get(0);
        if (!expectedAudience.equals(audience)) {
            throw new SecurityException("Invalid audience: " + audience);
        }
        
        // Verify expiration
        if (isExpired(claims)) {
            throw new SecurityException("Token has expired");
        }
    }

    /**
     * Verify JWT claims with multiple acceptable issuers and audiences
     */
    private void verifyClaimsMultiple(JWTClaimsSet claims, String[] expectedIssuers, String[] expectedAudiences) throws Exception {
        // Verify issuer
        String issuer = claims.getIssuer();
        boolean validIssuer = false;
        for (String expectedIssuer : expectedIssuers) {
            if (expectedIssuer.equals(issuer)) {
                validIssuer = true;
                break;
            }
        }
        if (!validIssuer) {
            throw new SecurityException("Invalid issuer: " + issuer);
        }
        
        // Verify audience
        String audience = claims.getAudience().get(0);
        boolean validAudience = false;
        for (String expectedAudience : expectedAudiences) {
            if (expectedAudience.equals(audience)) {
                validAudience = true;
                break;
            }
        }
        if (!validAudience) {
            throw new SecurityException("Invalid audience: " + audience);
        }
        
        // Verify expiration
        if (isExpired(claims)) {
            throw new SecurityException("Token has expired");
        }
    }

    /**
     * Clear the JWKS cache (useful for testing or forced refresh)
     */
    public void clearCache() {
        jwkSet = null;
        jwkSetLastFetched = null;
        cachedJwksUrl = null;
    }
}

