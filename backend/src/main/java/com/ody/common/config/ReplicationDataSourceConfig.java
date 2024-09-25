package com.ody.common.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Slf4j
@Profile("prod")
@Configuration
public class ReplicationDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSource replicationRouteDataSource) {
        return new LazyConnectionDataSourceProxy(replicationRouteDataSource);
    }

    @Bean
    public DataSource replicationRouteDataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource
    ) {
        HashMap<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(ReplicationType.WRITE, writeDataSource);
        dataSourceMap.put(ReplicationType.READ, readDataSource);

        ReplicationDataSourceRouter replicationDataSourceRouter = new ReplicationDataSourceRouter();
        replicationDataSourceRouter.setTargetDataSources(dataSourceMap);
        replicationDataSourceRouter.setDefaultTargetDataSource(readDataSource);
        return replicationDataSourceRouter;
    }
}
