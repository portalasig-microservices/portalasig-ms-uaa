package com.portalasig.ms.uaa.repository;


import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);

    List<RoleEntity> findAllByNameIn(List<String> name);

}
