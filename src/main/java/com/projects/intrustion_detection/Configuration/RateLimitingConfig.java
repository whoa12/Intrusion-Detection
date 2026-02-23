package com.projects.intrustion_detection.Configuration;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {
    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        String uri = "redis://127.0.0.1:6379";
        System.out.println("Connecting to Redis at: " + uri);
        return RedisClient.create(uri);
    }




    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> bucket4jConnection(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public LettuceBasedProxyManager<String> proxyManager(
            StatefulRedisConnection<String, byte[]> bucket4jConnection) {

        return LettuceBasedProxyManager.builderFor(bucket4jConnection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(
                                        Duration.ofMinutes(1)
                                )
                )
                .build();
    }
}
