package com.ody.common;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheCleaner {

    private static final String ALL_KEYS = "*";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void clear() {
        Set<String> keys = redisTemplate.keys(ALL_KEYS);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
