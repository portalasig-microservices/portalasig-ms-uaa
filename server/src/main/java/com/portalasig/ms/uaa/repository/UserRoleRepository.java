package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    @Query(value = "DELETE FROM user_role WHERE user_role_id in :ids", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void removeAllByIdsIn(@Param("ids") List<Long> ids);
}
