package com.ody.common.redis;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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

    public int getBitCount(String key, int startBit, int endBit) {
        int bitRange = endBit - startBit + 1;
        BitFieldSubCommands commands = BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(bitRange))
                .valueAt(startBit);

        List<Long> result = opsForValue().bitField(key, commands);
        return Long.bitCount(result.get(0));
    }
}
