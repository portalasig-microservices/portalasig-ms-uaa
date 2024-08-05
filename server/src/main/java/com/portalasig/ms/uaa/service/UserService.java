package com.portalasig.ms.uaa.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.portalasig.ms.commons.constants.Authority;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.commons.rest.exception.ConflictException;
import com.portalasig.ms.commons.rest.exception.ResourceNotFoundException;
import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.commons.rest.security.CurrentAuthentication;
import com.portalasig.ms.uaa.constant.RoleType;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.domain.entity.UserRoleEntity;
import com.portalasig.ms.uaa.dto.CsvUser;
import com.portalasig.ms.uaa.dto.RegisterRequest;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    public static final Set<String> defaultRoleTypes = Set.of(RoleType.USER.name(), RoleType.STUDENT.name());
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${ms.uaa.tools.users.csv.input-header}")
    private final HashSet<String> inputCsvHeader;

    private final UserConverter userConverter;
    private final CurrentAuthentication currentAuthentication;

    private static Set<String> getUserRoles(boolean getStudents, boolean getProfessors) {
        Set<String> roles = new HashSet<>();
        if (getStudents) {
            roles.add(Authority.STUDENT.getCode());
        }
        if (getProfessors) {
            roles.add(Authority.PROFESSOR.getCode());
        }
        if (!getStudents && !getProfessors) {
            roles.addAll(List.of(Authority.STUDENT.getCode(), Authority.PROFESSOR.getCode()));
        }
        return roles;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!userRepository.existsByIdentity(request.getIdentity())) {
            Set<RoleEntity> defaultRoleEntities = roleRepository.findAllByNameIn(defaultRoleTypes);
            UserEntity userEntity = UserEntity.builder().build();
            Set<UserRoleEntity> userRoleEntities = defaultRoleEntities
                    .stream()
                    .map(role -> UserRoleEntity.builder().role(role).user(userEntity).build())
                    .collect(Collectors.toSet());

            userEntity.setFirstName(request.getFirstName());
            userEntity.setLastName(request.getLastName());
            userEntity.setUsername(request.getUsername());
            userEntity.setIdentity(request.getIdentity());
            userEntity.setEmail(request.getEmail());
            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
            userEntity.setUsername(request.getUsername() == null ? request.getEmail() : request.getUsername());
            userEntity.setUserRoles(userRoleEntities);
            userEntity.setCreatedDate(Instant.now());
            userEntity.setUpdatedDate(Instant.now());

            log.info(
                    "Registering user: {} {} {}",
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getEmail()
            );
            return userMapper.toDto(userRepository.save(userEntity));
        } else {
            throw new ConflictException("User already exists");
        }
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> {
                    Set<UserRoleEntity> userRoles = user.getUserRoles();
                    List<SimpleGrantedAuthority> authorities = userRoles.stream()
                            .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                            .toList();
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            authorities
                    );
                }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void setDefaultUserData(UserRequest request, UserEntity userEntity) {
        if (request.getFirstName() == null) {
            request.setFirstName(userEntity.getFirstName());
        }
        if (request.getLastName() == null) {
            request.setLastName(userEntity.getLastName());
        }
    }

    @Transactional
    public User updateUser(Long identity, UserRequest request) {
        UserEntity userEntity = userRepository.findByIdentity(identity)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %s not found", identity)));
        setDefaultUserData(request, userEntity);
        userMapper.updateEntity(request, userEntity);
        userRepository.save(userEntity);

        return userMapper.toDto(userEntity);
    }

    public User findUserByIdentity(Long identity) {
        UserEntity user = userRepository.findByIdentity(identity)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %s not found", identity)));
        return userMapper.toDto(user);
    }

    public Paginated<User> findAll(
            boolean getStudents,
            boolean getProfessors,
            Pageable pageable
    ) {
        Set<String> roles = getUserRoles(getStudents, getProfessors);
        Page<UserEntity> users = userRepository.findAllUsers(roles, pageable);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return Paginated.wrap(users.map(userMapper::toDto));
    }

    @Transactional
    public void deleteUser(Long identity) {
        UserEntity user = userRepository.findByIdentity(identity)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %s not found", identity)));
        userRoleRepository.removeAllByIdsIn(user.getUserRoles().stream().map(UserRoleEntity::getUserRoleId).toList());
        user.setUserRoles(null);
        userRepository.delete(user);
    }

    @Transactional
    public void createUsersFromCsv(InputStream stream) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(stream))) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // readNext() method reads the line and skips it from the array
            String[] header = reader.readNext();
            validateHeader(Arrays.asList(header));
            List<CsvUser> csvUsers = new CsvToBeanBuilder<CsvUser>(reader)
                    .withType(CsvUser.class)
                    .build()
                    .parse();
            log.info("Starting users import from csv with user_size={}", csvUsers.size());
            List<UserEntity> userEntities = csvUsers
                    .stream()
                    .map(this::createUserFromCsv)
                    .toList();
            userRepository.saveAll(userEntities);
            stopWatch.stop();
            log.info("Import users from csv finished in {}ms", stopWatch.getTotalTimeMillis());
        } catch (CsvValidationException | IOException e) {
            throw new SystemErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Something went wrong while parsing csv file",
                    e
            );
        }
    }

    public UserEntity createUserFromCsv(CsvUser csvUser) {
        UserEntity user = userMapper.fromCsvUserToUserEntity(csvUser);
        userConverter.setCsvUserRoleOrDefault(user, csvUser);
        userConverter.setUserInformation(user);
        return user;
    }

    private void validateHeader(List<String> fileHeader) {
        HashSet<String> fileHeaderSet = new HashSet<>(fileHeader);
        if (!fileHeaderSet.containsAll(inputCsvHeader)) {
            throw new BadRequestException("Invalid csv header");
        }
    }
}
