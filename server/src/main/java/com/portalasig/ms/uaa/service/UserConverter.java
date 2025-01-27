package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.RoleType;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter {

    public void setCsvUserRoleOrDefault(UserEntity user, CsvUser csvUser, List<RoleEntity> roleEntities) {
        RoleType userRole = RoleType.fromCode(csvUser.getRole());
        Set<String> userRoles = new HashSet<>(RoleType.getDefaultRolesString());
        if (userRole != RoleType.INVALID) {
            userRoles.add(userRole.getCode());
        }
        Set<UserRoleEntity> userRoleEntities = roleEntities.stream()
                .filter(role -> userRoles.contains(role.getName()))
                .map(role -> UserRoleEntity.builder().role(role).user(user).build())
                .collect(Collectors.toSet());
        user.setUserRoles(userRoleEntities);
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
}
