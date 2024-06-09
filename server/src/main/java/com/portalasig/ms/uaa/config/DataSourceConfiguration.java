package com.portalasig.ms.uaa.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfiguration {

    private static HikariDataSource createDataSource(String poolName, String url) {
        HikariDataSource dataSource = new HikariDataSource();
        // This is necessary because HikariDataSource does not define a setUrl method,
        // so @ConfigurationProperties cannot configure it from the "url" configuration property.
        dataSource.setJdbcUrl(url);
        dataSource.setPoolName(poolName);
        return dataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        return createDataSource("portalAsigUAADataSource", dataSourceProperties.getUrl());
    }
}
