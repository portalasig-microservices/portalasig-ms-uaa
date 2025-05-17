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

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter {

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

    public void setUserInformation(UserEntity user, CsvUser csvUser, String defaultPassword) {
        if (user.getPassword() == null) {
            user.setPassword(defaultPassword);
        }
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }

        user.setEmailSettings(decomposeEmailSettingsBitMaskIntoString(csvUser.getEmailSettings()));
    }

    private String decomposeEmailSettingsBitMaskIntoString(Integer emailSettings) {
        if (emailSettings == null) {
            return EmailSetting.defaultEmailSettings();
        }

        Set<String> activeFeatures = new HashSet<>();
        // Start checking from the second bit (2^1) as (2^0) is not used
        // Bitmask is built using EmailSettings map index order
        for (int i = 0; i < EmailSetting.INDEX_TO_ENUM_MAPPER.size(); i++) {
            if ((emailSettings & (1 << (i + 1))) != 0) { // Check if the (i+1)th bit is set
                activeFeatures.add(EmailSetting.fromIndex(i).toString());
            }
        }
        return String.join(",", activeFeatures);
    }

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
