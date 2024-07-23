package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Set<RoleEntity> findAllByNameIn(Set<String> name);
}
