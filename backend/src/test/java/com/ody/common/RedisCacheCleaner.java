package com.ody.common;

import java.util.Set;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheCleaner {

    private static final String ALL_KEYS = "*";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public void clear() {
        redissonClient.getKeys().flushdb();
        Set<String> keys = redisTemplate.keys(ALL_KEYS);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
