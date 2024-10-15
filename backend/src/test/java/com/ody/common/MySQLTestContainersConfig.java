package com.ody.common;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class MySQLTestContainersConfig {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.35")
            .withDatabaseName("ody")
            .withCommand("--default-time-zone=+09:00")
            .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --default-time-zone=+09:00");

    static {
        mysqlContainer.start();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(mysqlContainer.getJdbcUrl())
                .username(mysqlContainer.getUsername())
                .password(mysqlContainer.getPassword())
                .driverClassName(mysqlContainer.getDriverClassName())
                .build();
    }
}
