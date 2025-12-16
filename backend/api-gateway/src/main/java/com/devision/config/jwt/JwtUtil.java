package com.devision.config.jwt;

import com.devision.config.JwtConfigProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;

@Component
public class JwtUtil {

    private final JwtConfigProperties props;
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public JwtUtil(JwtConfigProperties props) throws Exception {
        this.props = props;

        // Load keys from JWKS endpoint (supports RSA/EC)
        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(props.getJwksUrl()));

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSAlgorithmFamilyJWSKeySelector<>(
                JWSAlgorithm.Family.RSA, jwkSource
        ));

        // If you use ES256 instead, change RSA -> EC:
        // processor.setJWSKeySelector(new JWSAlgorithmFamilyJWSKeySelector<>(JWSAlgorithm.Family.EC, jwkSource));

        // Verify standard claims: iss + aud are mandatory; exp is checked by Nimbus
        processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder()
                        .issuer(props.getIssuer())
                        .build(),
                new DefaultJWTClaimsVerifier.RequiredClaim[]{
                        // none extra required here
                }
        ));

        this.jwtProcessor = processor;
    }

    public JWTClaimsSet parseAndVerify(String token) throws Exception {
        JWTClaimsSet claims = jwtProcessor.process(token, null);

        // audience check (contains)
        if (claims.getAudience() == null || !claims.getAudience().contains(props.getAudience())) {
            throw new SecurityException("Invalid audience");
        }

        return claims;
    }
}
