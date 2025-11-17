package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * Repository interface for managing {@link RoleEntity} persistence. Provides basic CRUD operations for role entities.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Retrieves all {@link RoleEntity} instances whose role is contained in the given set of {@link UserRole}s.
     *
     * @param roles
     *         a set of user roles to filter by
     * @return a set of matching {@link RoleEntity} objects
     */
    Set<RoleEntity> findAllByRoleIn(Set<UserRole> roles);
}
