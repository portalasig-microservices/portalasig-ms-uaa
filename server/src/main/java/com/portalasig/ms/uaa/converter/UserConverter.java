package com.portalasig.ms.uaa.converter;

import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts and maps user-related data between DTOs and domain entities. Includes logic for role assignment and email
 * setting decomposition.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter {

    /**
     * Sets the roles for a {@link UserEntity} based on the {@link CsvUser} input or default roles.
     *
     * @param user
     *         the target user entity
     * @param csvUser
     *         the source CSV user input
     * @param allRoles
     *         all available roles in the system
     */
    public void setCsvUserRoleOrDefault(UserEntity user, CsvUser csvUser, List<RoleEntity> allRoles) {
        UserRole userRole = UserRole.fromCode(csvUser.getRole());
        Set<UserRole> requiredRoles = new HashSet<>(Set.of(UserRole.STUDENT));
        if (userRole != UserRole.INVALID) {
            requiredRoles.add(userRole);
        }

        Map<UserRole, RoleEntity> roleMap = allRoles.stream()
                .collect(Collectors.toMap(RoleEntity::getRole, r -> r));

        Set<RoleEntity> assignedRoles = requiredRoles.stream()
                .map(roleMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        user.setRoles(assignedRoles);
    }

    /**
     * Sets basic user info such as password, username, and email settings.
     *
     * @param user
     *         the target user entity
     * @param csvUser
     *         the source CSV user
     * @param defaultPassword
     *         fallback password to assign if none is provided
     */
    public void setUserInformation(UserEntity user, CsvUser csvUser, String defaultPassword) {
        if (user.getPassword() == null) {
            user.setPassword(defaultPassword);
        }
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }

        user.setEmailSettings(decomposeEmailSettingsBitMaskIntoString(csvUser.getEmailSettings()));
    }

    /**
     * Converts a bitmask integer into a comma-separated string of active email settings.
     *
     * @param emailSettings
     *         the bitmask integer
     * @return a comma-separated string of enabled settings
     */
    private String decomposeEmailSettingsBitMaskIntoString(Integer emailSettings) {
        if (emailSettings == null) {
            return EmailSetting.defaultEmailSettings();
        }

        Set<String> activeFeatures = new HashSet<>();
        for (int i = 0; i < EmailSetting.INDEX_TO_ENUM_MAPPER.size(); i++) {
            if ((emailSettings & (1 << (i + 1))) != 0) {
                activeFeatures.add(EmailSetting.fromIndex(i).toString());
            }
        }
        return String.join(",", activeFeatures);
    }

    /**
     * Assigns a user role to the provided user entity using the full list of role entities.
     *
     * @param userRole
     *         the requested user role
     * @param allRoles
     *         all available roles
     * @param userEntity
     *         the target user entity
     */
    public void setUserRoles(UserRole userRole, List<RoleEntity> allRoles, UserEntity userEntity) {
        List<UserRole> incomingRoles = new ArrayList<>();

        if (userRole.isAllowed()) {
            incomingRoles.add(userRole);
        } else {
            incomingRoles.add(UserRole.STUDENT);
        }

        if (userEntity.getRoles() == null) {
            userEntity.setRoles(new HashSet<>());
        }

        Map<UserRole, RoleEntity> roleMap = allRoles.stream()
                .collect(Collectors.toMap(RoleEntity::getRole, role -> role));

        Set<RoleEntity> incomingRoleEntities = incomingRoles.stream()
                .map(role -> roleMap.getOrDefault(UserRole.fromCode(role.getCode()), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userEntity.setRoles(incomingRoleEntities);
    }
}
