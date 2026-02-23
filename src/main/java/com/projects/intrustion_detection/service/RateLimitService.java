package com.projects.intrustion_detection.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final LettuceBasedProxyManager<String> proxyManager;

    public Bucket resolveBucket(String key){
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(3).refillGreedy(3, Duration.ofSeconds(10)))
                                                                .build();

        return proxyManager.builder().build(key, configuration);
    }
}
