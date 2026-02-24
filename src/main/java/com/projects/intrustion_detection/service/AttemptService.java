    package com.projects.intrustion_detection.service;

    import com.projects.intrustion_detection.Entity.Attack;
    import com.projects.intrustion_detection.repository.AttackRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.stereotype.Service;

    import java.time.Duration;
    import java.time.LocalDateTime;

    @Service
    @RequiredArgsConstructor
    public class AttemptService {
        private final StringRedisTemplate redisTemplate;
        private final int  limit = 5;
        private final Duration blockTime = Duration.ofMinutes(5);
        private final AttackRepository attackRepository;

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

        public void logAttack(String uri, String ipAddress, String email){
            Attack attack = new Attack();
            attack.setAttackType("Brute-Force Login attempt");
            attack.setUri(uri);
            attack.setIpAddress(ipAddress);
            attack.setPayload("email used: "+email);
            attack.setTimeStamp(LocalDateTime.now());
            attack.setBlocked(true);
            attackRepository.save(attack);
        }
    }
