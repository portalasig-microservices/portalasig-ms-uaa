package com.portalasig.ms.uaa.mapper;

import com.portalasig.ms.uaa.constant.RoleType;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Mapper for the entity {@link UserEntity} and its DTO {@link User}.
 */
@Mapper(
        imports = {Instant.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    List<RoleType> EXCLUDED_ROLES = List.of(RoleType.USER, RoleType.ADMIN);

    /**
     * Sets the date with a default value if the provided date string is null.
     *
     * @param dateString
     *         the date string to convert
     * @return the converted date as an {@link Instant}
     */
    @Named("setDateWithDefault")
    static Instant setDateWithDefault(String dateString) {
        if (dateString == null) {
            return Instant.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
        return offsetDateTime.toInstant();
    }

    /**
     * Converts a set of {@link UserRoleEntity} to a list of {@link UserRole}, excluding certain roles.
     *
     * @param userRoles
     *         the set of user role entities to convert
     * @return the list of user roles
     */
    @Named("fromUserEntityRolesToUserRoles")
    static List<UserRole> fromUserEntityRolesToUserRoles(Set<UserRoleEntity> userRoles) {
        return userRoles
                .stream()
                .filter(userRole -> !EXCLUDED_ROLES.contains(RoleType.fromCode(userRole.getRole().getName())))
                .map(userRole -> UserRole.fromCode(userRole.getRole().getName()))
                .toList();
    }

    /**
     * Updates an existing {@link UserEntity} with data from a {@link UserRequest}, preserving existing values if not
     * provided.
     *
     * @param request
     *         the request containing updated data
     * @param userEntity
     *         the entity to be updated
     */
    static void updateEntityWithRequestData(UserRequest request, @MappingTarget UserEntity userEntity) {
        if (request.getFirstName() != null) {
            userEntity.setFirstName(request.getFirstName());
        } else {
            request.setFirstName(userEntity.getFirstName());
        }
        if (request.getLastName() != null) {
            userEntity.setLastName(request.getLastName());
        } else {
            request.setLastName(userEntity.getLastName());
        }
    }

    /**
     * Converts a {@link UserEntity} to a {@link User} DTO.
     *
     * @param userEntity
     *         the user entity to convert
     * @return the converted user DTO
     */
    @Mapping(source = "userRoles", target = "roles", qualifiedByName = "fromUserEntityRolesToUserRoles")
    User toDto(UserEntity userEntity);

    /**
     * Updates an existing {@link UserEntity} with data from a {@link UserRequest}.
     *
     * @param request
     *         the request containing updated data
     * @param existingEntity
     *         the entity to be updated
     */
    default void updateEntity(UserRequest request, UserEntity existingEntity) {
        updateEntityWithRequestData(request, existingEntity);
    }

    /**
     * Converts a {@link CsvUser} to a {@link UserEntity}.
     *
     * @param user
     *         the CSV user to convert
     * @return the converted user entity
     */
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "createdDate", qualifiedByName = "setDateWithDefault")
    @Mapping(target = "updatedDate", qualifiedByName = "setDateWithDefault")
    UserEntity fromCsvUserToUserEntity(CsvUser user);
}
