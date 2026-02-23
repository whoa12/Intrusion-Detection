    package com.projects.intrustion_detection.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.stereotype.Service;

    import java.time.Duration;

    @Service
    @RequiredArgsConstructor
    public class AttemptService {
        private final StringRedisTemplate redisTemplate;
        private final int  limit = 5;
        private final Duration blockTime = Duration.ofMinutes(5);

        public void trackIp(String ip){
            String key = "login:fail:" + ip;
            Long count = redisTemplate.opsForValue().increment(key);

            if (count != null && count == 1) {
                redisTemplate.expire(key, blockTime);
            }
        }

        public boolean isBlocked(String ip) {
            String key = "login:fail:" + ip;
            String value = redisTemplate.opsForValue().get(key);
            return value != null && Long.parseLong(value) >= limit;
        }

        public void resetIp(String ip) {
            redisTemplate.delete("login:fail:" + ip);
        }
    }
