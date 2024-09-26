package com.ody.common.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy repairMigrateStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
