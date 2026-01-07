package com.devision.authentication.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory revocationRedisConnectionFactory(
            @Value("${app.redis.revocation.host}") String host,
            @Value("${app.redis.revocation.port}") int port,
            @Value("${app.redis.revocation.password:}") String password
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (password != null && !password.isBlank()) {
            config.setPassword(RedisPassword.of(password));
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate revocationRedisTemplate(
            @Qualifier("revocationRedisConnectionFactory") RedisConnectionFactory factory
    ) {
        return new StringRedisTemplate(factory);
    }
}