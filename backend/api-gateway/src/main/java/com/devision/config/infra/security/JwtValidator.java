package com.devision.config.infra.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtValidator {

    private final RemoteJWKSet<?> jwkSet;

    private final String issuer;
    private final String audience;

    public JwtValidator(
            @Value("${security.jwt.jwks-url}") String jwksUrl,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience
    ) throws Exception {
        this.jwkSet = new RemoteJWKSet<>(new URL(jwksUrl));
        this.issuer = issuer;
        this.audience = audience;
    }

    public JWTClaimsSet validateAndGetClaims(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);
        String kid = jwt.getHeader().getKeyID();

        // load public key from JWKS by kid
        JWK jwk = jwkSet.get(jwt.getHeader(), null).getKeys().stream()
                .filter(k -> k.getKeyID() != null && k.getKeyID().equals(kid))
                .findFirst()
                .orElseThrow(() -> new SecurityException("No matching JWK for kid=" + kid));

        JWSVerifier verifier;
        if (jwk instanceof RSAKey rsaKey) {
            verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        } else if (jwk instanceof ECKey ecKey) {
            verifier = new ECDSAVerifier(ecKey.toECPublicKey());
        } else {
            throw new SecurityException("Unsupported key type: " + jwk.getKeyType());
        }

        if (!jwt.verify(verifier)) {
            throw new SecurityException("Invalid JWT signature");
        }

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        verifyStandardClaims(claims);

        return claims;
    }

    private void verifyStandardClaims(JWTClaimsSet claims) {
        // exp
        Date exp = claims.getExpirationTime();
        if (exp == null || exp.toInstant().isBefore(Instant.now())) {
            throw new SecurityException("Token expired");
        }

        // iss
        if (claims.getIssuer() == null || !claims.getIssuer().equals(issuer)) {
            throw new SecurityException("Invalid issuer");
        }

        // aud
        if (claims.getAudience() == null || !claims.getAudience().contains(audience)) {
            throw new SecurityException("Invalid audience");
        }
    }
}
