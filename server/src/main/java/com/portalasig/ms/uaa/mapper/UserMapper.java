package com.portalasig.ms.uaa.mapper;

import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Mapper for the entity {@link UserEntity} and its DTO {@link User}.
 */
@Mapper(imports = {Instant.class}, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Converts a {@link UserRequest} to a {@link UserEntity}.
     *
     * @param request
     *         the user request to convert
     * @return the converted user entity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailSettings", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(UserRequest request);

    /**
     * Converts a {@link UserRequest} to an existing {@link UserEntity}.
     *
     * @param userEntity
     *         the existing user entity to update
     * @param userRequest
     *         the user request to convert
     * @return the updated user entity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailSettings", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntityFromExisting(@MappingTarget UserEntity userEntity, UserRequest userRequest);

    /**
     * Converts a {@link UserEntity} to a {@link User} DTO.
     *
     * @param userEntity
     *         the user entity to convert
     * @return the converted user DTO
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    User toDto(UserEntity userEntity);

    /**
     * Converts a {@link CsvUser} to a {@link UserEntity}.
     *
     * @param user
     *         the CSV user to convert
     * @return the converted user entity
     */
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    UserEntity fromCsvUserToUserEntity(CsvUser user);

    /**
     * Decodes the email settings from a string to a list of {@link EmailSetting}.
     *
     * @param emailSettings
     *         the email settings string to decode
     * @return the list of email settings
     */
    default List<EmailSetting> decodeEmailSettingsFromString(String emailSettings) {
        return emailSettings != null ?
                Arrays
                        .stream(emailSettings.split(","))
                        .map(EmailSetting::fromCode)
                        .toList() :
                new ArrayList<>();
    }

    /**
     * Sets the date with a default value if the provided date string is null.
     *
     * @param dateString
     *         the date string to convert
     * @return the converted date as an {@link Instant}
     */
    default Instant setDateWithDefault(String dateString) {
        if (dateString == null) {
            return Instant.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
        return offsetDateTime.toInstant();
    }

    /**
     * Converts a set of {@link RoleEntity} to a list of {@link UserRole}, excluding certain roles.
     *
     * @param userRoles
     *         the set of user role entities to convert
     * @return the list of user roles
     */
    default List<UserRole> fromRoleEntityToUserRole(Set<RoleEntity> userRoles) {
        return userRoles.stream()
                .map(entity -> UserRole.fromCode(entity.getRole().getCode())).toList();
    }
}
