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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Slf4j
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
    @DependsOn("replicationRouteDataSource")
    public DataSource dataSource(DataSource replicationRouteDataSource) {
        log.info("데이터 소스 초기화 롼료 !!");
        return new LazyConnectionDataSourceProxy(replicationRouteDataSource);
    }

    @Bean
    @DependsOn({"writeDataSource", "readDataSource"})
    public DataSource replicationRouteDataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource
    ) {
        HashMap<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(ReplicationType.WRITE, writeDataSource);
        dataSourceMap.put(ReplicationType.READ, readDataSource);

        ReplicationDataSourceRouter replicationDataSourceRouter = new ReplicationDataSourceRouter();
        replicationDataSourceRouter.setTargetDataSources(dataSourceMap);
        replicationDataSourceRouter.setDefaultTargetDataSource(writeDataSource);
        return replicationDataSourceRouter;
    }
}
