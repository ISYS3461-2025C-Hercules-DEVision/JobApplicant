package com.devision.authentication.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    @Qualifier("revocationRedisTemplate")
    private final StringRedisTemplate redis;

    private static final String PREFIX = "revoked:jti:";

    public void revoke(String jti, long ttlMs) {
        redis.opsForValue().set(PREFIX + jti, "1", Duration.ofMillis(ttlMs));
    }

    public boolean isRevoked(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
    }
}