package com.ody.route.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteClientRedisTemplate extends RedisTemplate<String, String> {

    @Autowired
    public RouteClientRedisTemplate(RedisConnectionFactory connectionFactory) {
        this.setKeySerializer(RedisSerializer.string());
        this.setValueSerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setHashValueSerializer(RedisSerializer.string());
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    public int increment(String key) {
        return Optional.ofNullable(opsForValue().increment(key))
                .map(Long::intValue)
                .orElse(0);
    }

    public int getKeyCount(String key) {
        return Optional.ofNullable(opsForValue().get(key))
                .map(Integer::parseInt)
                .orElse(0);
    }
}
