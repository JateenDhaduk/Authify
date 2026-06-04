package com.example.Authify.security;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RateLimitConfig {

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${app.redis.ssl:false}")
    private boolean redisSsl;

    @Bean
    public RedisClient redisClient(){
        String scheme = redisSsl ? "rediss://" : "redis://";
        String redisUri = (redisPassword == null || redisPassword.isEmpty()) 
                ? scheme + redisHost + ":" + redisPort 
                : scheme + ":" + redisPassword + "@" + redisHost + ":" + redisPort;
        return RedisClient.create(redisUri);
    }

    @Bean
    public ProxyManager<byte[]> proxyManager(RedisClient redisClient){
        return LettuceBasedProxyManager.builderFor(redisClient).withExpirationStrategy(
                io.github.bucket4j.distributed.ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(java.time.Duration.ofMinutes(5))
        ).build();
    }
    // ── Spring RedisTemplate using SAME Lettuce connection ────
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }
        org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = 
                org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.builder();
        if (redisSsl) {
            builder.useSsl();
        }
        return new LettuceConnectionFactory(config, builder.build());
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }
}
