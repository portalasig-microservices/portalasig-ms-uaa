package com.portalasig.ms.uaa.mapper;

import com.portalasig.ms.uaa.constant.EmailSetting;
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
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Mapper for the entity {@link UserEntity} and its DTO {@link User}.
 */
@Mapper(imports = {Instant.class}, componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<RoleType> EXCLUDED_ROLES = List.of(RoleType.USER);

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
        return userRoles.stream()
                .filter(userRole -> !EXCLUDED_ROLES.contains(RoleType.fromCode(userRole.getRole().getName())))
                .map(userRole -> UserRole.fromCode(userRole.getRole().getName())).toList();
    }

    /**
     * Converts a {@link UserEntity} to a {@link User} DTO.
     *
     * @param userEntity
     *         the user entity to convert
     * @return the converted user DTO
     */
    @Mapping(source = "userRoles", target = "roles", qualifiedByName = "fromUserEntityRolesToUserRoles")
    @Mapping(source = "emailSettings", target = "emailSettings", qualifiedByName = "decodeEmailSettingsFromString")
    User toDto(UserEntity userEntity);

    /**
     * Updates an existing {@link UserEntity} with data from a {@link UserRequest}.
     *
     * @param request
     *         the request containing updated data
     * @param entity
     *         the entity to be updated
     */
    default void updateEntity(UserRequest request, UserEntity entity) {
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        } else {
            request.setFirstName(entity.getFirstName());
        }
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        } else {
            request.setLastName(entity.getLastName());
        }

        if (request.getEmailSettings() != null) {
            String emailSettings = request
                    .getEmailSettings()
                    .stream()
                    .map(EmailSetting::getCode)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            entity.setEmailSettings(emailSettings);
        } else {
            request.setLastName(entity.getLastName());
        }
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

    @Named("decodeEmailSettingsFromString")
    default List<EmailSetting> decodeEmailSettingsFromString(String emailSettings) {
        return Arrays
                .stream(emailSettings.split(","))
                .map(EmailSetting::fromCode)
                .toList();
    }
}
