package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.constant.RoleType;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import com.portalasig.ms.uaa.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter {

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${ms.uaa.tools.users.default-user}")
    private final String defaultUser;

    public void setCsvUserRoleOrDefault(UserEntity user, CsvUser csvUser) {
        Set<String> roles = new HashSet<>(UserService.defaultRoleTypes);
        RoleType roleType = RoleType.fromCode(csvUser.getRole());
        if (roleType != RoleType.INVALID) {
            roles.add(roleType.getCode());
        }
        Set<RoleEntity> roleEntities = roleRepository.findAllByNameIn(roles);
        Set<UserRoleEntity> userRoleEntities = roleEntities.stream()
                .map(role -> UserRoleEntity.builder().role(role).user(user).build()).collect(Collectors.toSet());
        user.setUserRoles(userRoleEntities);
    }

    public void setUserInformation(UserEntity user) {
        if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode(defaultUser));
        }
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }
    }
}
