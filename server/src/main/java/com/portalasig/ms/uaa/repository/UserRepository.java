package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.domain.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    Optional<UserEntity> findByEmail(@NotNull String email);

    @Query(value = """
            SELECT u
            FROM UserEntity u
            WHERE
                str(u.identity) LIKE concat(:query, '%')
                OR lower(u.email) LIKE lower(concat(:query, '%'))
                OR lower(u.firstName) LIKE lower(concat(:query, '%'))
                OR lower(u.lastName) LIKE lower(concat(:query, '%'))
                OR lower(concat(u.firstName, ' ', u.lastName)) LIKE lower(concat(:query, '%'))
            """)
    List<UserEntity> smartSearchUsers(@Param("query") String query, Pageable pageable);
}
