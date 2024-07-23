package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String username);

    boolean existsByIdentity(Long identity);

    Optional<UserEntity> findByIdentity(Long identity);
}
