package com.example.Authify.service;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {
    @Autowired
    private ProxyManager<byte[]> manager;

    public BucketProxy resolveBucket(String ipAddress){
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(limit->limit.capacity(3).refillIntervally(1,Duration.ofMinutes(1)))
                .build();

        return manager.builder().build(ipAddress.getBytes(),() -> configuration);
    }
}
