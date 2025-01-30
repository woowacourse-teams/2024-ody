package com.ody.common;

import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class RedisTestContainersConfig {

    private static final int REDIS_PORT = 6379;

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.4.1-alpine3.20")
            .withExposedPorts(REDIS_PORT)
            .withCommand("redis-server --notify-keyspace-events Ex")
            .withStartupTimeout(Duration.ofSeconds(60));

    static {
        redisContainer.start();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redisContainer.getHost(),
                redisContainer.getMappedPort(REDIS_PORT)
        );
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }
}
