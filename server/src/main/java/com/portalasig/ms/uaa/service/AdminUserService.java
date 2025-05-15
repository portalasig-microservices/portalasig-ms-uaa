package com.portalasig.ms.uaa.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.portalasig.ms.commons.constants.Authority;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.commons.rest.exception.ResourceNotFoundException;
import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.converter.UserConverter;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.mapper.UserMapper;
import com.portalasig.ms.uaa.repository.RoleRepository;
import com.portalasig.ms.uaa.repository.UserRepository;
import com.portalasig.ms.uaa.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${ms.uaa.tools.users.csv.input-header}")
    private final HashSet<String> inputCsvHeader;

    private final UserConverter userConverter;

    @Value("${ms.uaa.tools.users.default-user}")
    private final String defaultUser;

    @Value("${ms.uaa.tools.users.default-password}")
    private final String defaultPassword;

    @PreAuthorize("hasAuthority('ADMIN')")
    public Paginated<User> findAll(boolean studentsOnly, boolean professorsOnly, Pageable pageable) {
        Set<String> roles = getUserRoles(studentsOnly, professorsOnly);
        Page<UserEntity> users = userRepository.findAllUsers(roles, pageable);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return Paginated.wrap(users.map(userMapper::toDto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getUsers(List<Long> identities) {
        List<UserEntity> allUsers = userRepository.findAllByIdentity(identities);
        return allUsers.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(Long identity) {
        UserEntity user = userRepository.findByIdentity(identity)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %s not found", identity)));
        userRoleRepository.removeAllByIdsIn(user.getUserRoles().stream().map(UserRoleEntity::getUserRoleId).toList());
        user.setUserRoles(null);
        userRepository.delete(user);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void createUsersFromCsv(InputStream stream) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(stream))) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // readNext() method reads the line and skips it from the array
            String[] header = reader.readNext();
            validateHeader(Arrays.asList(header));
            List<CsvUser> csvUsers = new CsvToBeanBuilder<CsvUser>(reader).withType(CsvUser.class).build().parse();
            log.info("Starting users import from csv with user_size={}", csvUsers.size());
            List<RoleEntity> roleEntities = roleRepository.findAll();
            String defaultPassword = passwordEncoder.encode(defaultUser);
            List<UserEntity> userEntities = csvUsers
                    .stream()
                    .map(csvUser -> createUserFromCsv(csvUser, roleEntities, defaultPassword))
                    .toList();
            userRepository.saveAll(userEntities);
            stopWatch.stop();
            log.info("Import users from csv finished in {}ms", stopWatch.getTotalTimeMillis());
        } catch (CsvValidationException | IOException e) {
            throw new SystemErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Something went wrong while parsing csv file", e);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public User upsertUser(UserRequest userRequest) {
        UserEntity userEntity;
        Optional<UserEntity> existingUser = userRepository.findByIdentity(userRequest.getIdentity());
        if (existingUser.isEmpty()) {
            userEntity = userMapper.toEntity(userRequest);
            userEntity.setEmailSettings(EmailSetting.defaultEmailSettings());
            userEntity.setPassword(passwordEncoder.encode(defaultPassword));
            userEntity.setUsername(userRequest.getIdentity().toString());
        } else {
            userEntity = userMapper.toEntityFromExisting(existingUser.get(), userRequest);
        }
        List<RoleEntity> roleEntities = roleRepository.findAll();
        userConverter.setUserRoles(userRequest.getUserRole(), roleEntities, userEntity);
        userEntity.setCreatedDate(Instant.now());
        userEntity.setUpdatedDate(Instant.now());
        userEntity = userRepository.save(userEntity);
        log.info("Upserted user_id={}", userEntity.getIdentity());
        return userMapper.toDto(userEntity);
    }

    private UserEntity createUserFromCsv(CsvUser csvUser, List<RoleEntity> roleEntities, String defaultPassword) {
        UserEntity user = userMapper.fromCsvUserToUserEntity(csvUser);
        userConverter.setCsvUserRoleOrDefault(user, csvUser, roleEntities);
        userConverter.setUserInformation(user, csvUser, defaultPassword);
        return user;
    }

    private void validateHeader(List<String> fileHeader) {
        HashSet<String> fileHeaderSet = new HashSet<>(fileHeader);
        if (!fileHeaderSet.containsAll(inputCsvHeader)) {
            throw new BadRequestException("Invalid csv header");
        }
    }

    private static Set<String> getUserRoles(boolean studentsOnly, boolean professorsOnly) {
        Set<String> roles = new HashSet<>();
        if (studentsOnly) {
            roles.add(Authority.STUDENT.getCode());
        }
        if (professorsOnly) {
            roles.add(Authority.PROFESSOR.getCode());
        }
        if (!studentsOnly && !professorsOnly) {
            roles.addAll(List.of(Authority.STUDENT.getCode(), Authority.PROFESSOR.getCode()));
        }
        return roles;
    }
}
