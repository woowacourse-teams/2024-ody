package com.ody.route.repository;

import com.ody.common.exception.OdyServerErrorException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Slf4j
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
                .orElseThrow(() -> {
                    log.error("redis key 값({})이 존재하지 않아 증가시킬 수 없습니다.", key);
                    return new OdyServerErrorException("서버에 장애가 발생했습니다.");
                });
    }

    public int getKeyCount(String key) {
        return Optional.ofNullable(opsForValue().get(key))
                .map(Integer::parseInt)
                .orElse(0);
    }
}
