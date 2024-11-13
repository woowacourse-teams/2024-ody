package com.ody.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomRedisTemplate extends RedisTemplate<String, String> {

    @Autowired
    public CustomRedisTemplate(RedisConnectionFactory connectionFactory) {
        this.setKeySerializer(RedisSerializer.string());
        this.setValueSerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setHashValueSerializer(RedisSerializer.string());
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    public Long getBitCount(String key, long startBitIndex, long endBitIndex) {
        long startByteIndex = toByteIndex(startBitIndex);
        long endByteIndex = toByteIndex(endBitIndex);

        return execute((RedisCallback<Long>) connection ->
                connection.stringCommands().bitCount(key.getBytes(), startByteIndex, endByteIndex)
        );
    }

    private long toByteIndex(long bitIndex) {
        return bitIndex / 8;
    }
}
