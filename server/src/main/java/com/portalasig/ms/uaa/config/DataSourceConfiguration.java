package com.portalasig.ms.uaa.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for setting up the application's primary datasource using HikariCP. This class binds
 * configuration properties and defines how the {@link HikariDataSource} is initialized.
 */
@Configuration
public class DataSourceConfiguration {

    /**
     * Creates a HikariDataSource with a specified pool name and JDBC URL.
     *
     * @param poolName
     *         the name of the Hikari connection pool
     * @param url
     *         the JDBC URL for the database
     * @return a configured {@link HikariDataSource}
     */
    private static HikariDataSource createDataSource(String poolName, String url) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setPoolName(poolName);
        return dataSource;
    }

    /**
     * Defines the primary datasource bean for the application. Binds to the properties defined under
     * <code>spring.datasource</code>.
     *
     * @param dataSourceProperties
     *         the Spring Boot-managed datasource properties
     * @return the configured {@link HikariDataSource}
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        return createDataSource("portalAsigUAADataSource", dataSourceProperties.getUrl());
    }
}
