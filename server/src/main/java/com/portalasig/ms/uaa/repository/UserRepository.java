package com.portalasig.ms.uaa.repository;

import com.portalasig.ms.uaa.constant.UserRole;
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

/**
 * Repository interface for managing {@link UserEntity} persistence. Provides CRUD operations and custom queries for
 * users.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Checks if a user with the given identity exists.
     *
     * @param identity
     *         unique identity number
     * @return true if a user with the identity exists, false otherwise
     */
    boolean existsByIdentity(Long identity);

    /**
     * Finds a user by their identity number.
     *
     * @param identity
     *         unique identity number
     * @return an Optional containing the user if found
     */
    Optional<UserEntity> findByIdentity(Long identity);

    /**
     * Retrieves a page of users who have any of the specified roles.
     *
     * @param roles
     *         a set of user roles to match
     * @param pageable
     *         pagination information
     * @return a page of matching users
     */
    @Query("""
            SELECT user
            FROM UserEntity user
            JOIN user.roles role
            WHERE role.role IN :roles
            """)
    Page<UserEntity> findAllUsers(@Param("roles") Set<UserRole> roles, Pageable pageable);

    /**
     * Finds a user by their email address.
     *
     * @param email
     *         the user's email
     * @return an Optional containing the user if found
     */
    Optional<UserEntity> findByEmail(@NotNull String email);

    /**
     * Performs a case-insensitive search for users by various fields: identity, email, first/last name, or full name.
     * Only users with roles in {@code userRoles} are returned.
     *
     * @param query
     *         search string to match against user fields
     * @param userRoles
     *         roles to filter the users
     * @param pageable
     *         pagination information
     * @return a list of matching users
     */
    @Query(value = """
            SELECT DISTINCT u
            FROM UserEntity u
            JOIN u.roles r
            WHERE (
                str(u.identity) LIKE concat(:query, '%')
                OR lower(u.email) LIKE lower(concat(:query, '%'))
                OR lower(u.firstName) LIKE lower(concat(:query, '%'))
                OR lower(u.lastName) LIKE lower(concat(:query, '%'))
                OR lower(concat(u.firstName, ' ', u.lastName)) LIKE lower(concat(:query, '%'))
            )
            AND r.role IN (:userRoles)
            """)
    List<UserEntity> smartSearchUsers(
            @Param("query") String query,
            @Param("userRoles") List<UserRole> userRoles,
            Pageable pageable);

    /**
     * Retrieves all users by their identity numbers.
     *
     * @param identities
     *         list of user identities
     * @return a list of users matching the identities
     */
    @Query("""
            SELECT u
            FROM UserEntity u
            WHERE u.identity IN :identities
            """)
    List<UserEntity> findAllByIdentity(@Param("identities") List<Long> identities);
}
