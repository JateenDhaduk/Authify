package com.example.Authify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    @Autowired
    private final RedisTemplate<String , String > redisTemplate;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private static final String TOKEN_PREFIX = "refresh:token:";
    private static final String USER_PREFIX  = "refresh:user:";

    public String generateRefreshToken(String email){
        revokeByEmail(email);

        String token = UUID.randomUUID().toString();
        long ttlSeconds = refreshExpirationMs / 1000;

        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token,   // key
                email,                  // value
                ttlSeconds,             // TTL
                TimeUnit.SECONDS        // auto-expires — no cleanup needed
        );
        redisTemplate.opsForValue().set(
                USER_PREFIX + email,
                token,
                ttlSeconds,
                TimeUnit.SECONDS
        );

        log.info("Refresh token created for: {}", email);
        return token;
    }
    public String validateRefreshToken(String token){
        String email = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (email == null) {
            throw new RuntimeException(
                    "Refresh token is invalid or expired. Please log in again.");
        }

        log.info("Refresh token validated for: {}", email);
        return email;
    }
    private void tokenRevoke(String token){
        String email = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if(email != null){
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(USER_PREFIX + email);
            log.info("All tokens revoked for: {}", email);
        }
    }

    private void revokeByEmail(String email) {
        String token = redisTemplate.opsForValue().get(USER_PREFIX + email);

        if(token != null){
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(USER_PREFIX + email);
            log.info("All tokens revoked for: {}", email);
        }
    }
}
