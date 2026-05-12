package com.portalasig.ms.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for the UAA microservice.
 * <p>
 * This class serves as the entry point for the Spring Boot application. It includes configurations
 * for component scanning, JPA repositories, and entity scanning to ensure all parts of the
 * application are correctly initialized.
 * </p>
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.portalasig.ms.uaa.repository",
        "com.portalasig.ms.commons.lib.repository"
})
@EntityScan(basePackages = {
        "com.portalasig.ms.uaa.domain.entity",
        "com.portalasig.ms.commons.lib.domain.entity"
})
@ComponentScan(basePackages = {
        "com.portalasig.ms.uaa",
        "com.portalasig.ms.commons", // <--- CORRECCIÓN AQUÍ
        "com.portalasig.ms.notify.client",
        "com.portalasig.ms.notify.config",
})
@ConfigurationPropertiesScan(basePackages = {
        "com.portalasig.ms.uaa.config.properties"
})
@EnableDiscoveryClient
public class ServerApplication {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
