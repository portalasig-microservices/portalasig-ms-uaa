package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.domain.constant.RoleType;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.repository.RoleRepository;
import com.portalasig.ms.uaa.repository.UserRepository;
import com.portalasig.ms.uaa.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final List<String> defaultRoleTypes = List.of(RoleType.USER.toString());

    private final UserMapper userMapper;

    public UserEntity registerUser(RegisterRequest request) {
        if (!userRepository.existsByUsername(request.getEmail())) {
            List<RoleEntity> defaultRoleEntities = roleRepository.findAllByNameIn(defaultRoleTypes);
            UserEntity userEntity = UserEntity.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .username(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(defaultRoleEntities)
                    .createdDate(Instant.now())
                    .updatedDate(Instant.now())
                    .build();
            log.info(
                    "Registering user: {} {} {}",
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getUsername()
            );
            return userRepository.save(userEntity);
        } else {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "User already exists");
        }
    }

    public RoleEntity saveRole(RoleEntity roleEntity) {
        log.info("Saving role: {}", roleEntity);
        return roleRepository.save(roleEntity);
    }

    public void addRoleToUser(String username, String roleName) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        RoleEntity roleEntity = roleRepository.findByName(roleName).orElseThrow();
        userEntity.getRoles().add(roleEntity);
        log.info("Adding role {} to user {}", roleName, username);
        userRepository.save(userEntity);
    }

    public UserEntity getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("User with username %s not found", username))
                );
        return userMapper.toDto(user);
    }
}
