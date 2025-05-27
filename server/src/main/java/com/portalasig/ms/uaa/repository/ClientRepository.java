package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link ClientEntity} persistence. Provides methods to perform CRUD operations and
 * client lookup by client ID.
 */
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    /**
     * Finds a client entity by its unique client ID.
     *
     * @param clientId
     *         the client identifier
     * @return an {@link Optional} containing the found {@link ClientEntity}, or empty if not found
     */
    Optional<ClientEntity> findByClientId(String clientId);
}
