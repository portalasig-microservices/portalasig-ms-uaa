package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByClientId(String clientId);
}
