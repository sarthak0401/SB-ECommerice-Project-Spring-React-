package org.ecommerce.project.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisRateLimiterService {
    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int capacity, int refillTokens, long refillSeconds) {

        String redisKey = "rate_limit:" + key;

        List<Object> values = redisTemplate.opsForHash()
                .multiGet(redisKey, List.of("tokens", "lastRefill"));

        long now = System.currentTimeMillis();

        double tokens;
        long lastRefill;

        if (values.get(0) == null || values.get(1) == null) {
            tokens = capacity;
            lastRefill = now;
        } else {
            tokens = Double.parseDouble(values.get(0).toString());
            lastRefill = Long.parseLong(values.get(1).toString());

            double refill = ((now - lastRefill) / 1000.0) * (refillTokens / (double) refillSeconds);
            tokens = Math.min(capacity, tokens + refill);
        }

        if (tokens < 1) {
            return false;
        }

        tokens--;

        redisTemplate.opsForHash().put(redisKey, "tokens", String.valueOf(tokens));
        redisTemplate.opsForHash().put(redisKey, "lastRefill", String.valueOf(now));
        redisTemplate.expire(redisKey, Duration.ofMinutes(10));

        return true;
    }
}
