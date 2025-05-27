package com.portalasig.ms.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point for the PortalAsig UAA (User Authentication and Authorization) microservice.
 * <p>
 * This application handles user management, authentication, authorization, and related operations.
 * </p>
 *
 * <p>
 * It enables:
 * <ul>
 *     <li>JPA repositories via {@link EnableJpaRepositories}</li>
 *     <li>JPA auditing for automatic population of created/modified timestamps</li>
 *     <li>Component scanning for both commons and UAA packages</li>
 * </ul>
 * </p>
 */
@EnableJpaRepositories
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"com.portalasig.ms.commons", "com.portalasig.ms.uaa"})
public class ServerApplication {

    /**
     * Main method to bootstrap the UAA microservice.
     *
     * @param args
     *         application arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
