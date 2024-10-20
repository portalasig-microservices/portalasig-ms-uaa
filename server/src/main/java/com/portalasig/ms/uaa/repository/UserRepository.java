package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByIdentity(Long identity);

    Optional<UserEntity> findByIdentity(Long identity);

    @Query("""
            SELECT user
            FROM UserEntity user
            JOIN user.userRoles userRole
            WHERE userRole.role.name IN :roles
            """)
    Page<UserEntity> findAllUsers(@Param("roles") Set<String> roles, Pageable pageable);
}
