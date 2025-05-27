package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link RoleEntity} persistence. Provides basic CRUD operations for role entities.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
