package com.portalasig.ms.uaa.service;

import com.portalasig.ms.commons.rest.exception.ConflictException;
import com.portalasig.ms.commons.rest.exception.ResourceNotFoundException;
import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.notify.constant.EmailTemplate;
import com.portalasig.ms.notify.dto.EmailRequest;
import com.portalasig.ms.notify.operation.EmailOperations;
import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.EmailAddressRequest;
import com.portalasig.ms.uaa.dto.EmailSettingRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserEditPasswordRequest;
import com.portalasig.ms.uaa.dto.UserRestorePasswordRequest;
import com.portalasig.ms.uaa.email.template.PasswordRecoveryTemplate;
import com.portalasig.ms.uaa.mapper.UserMapper;
import com.portalasig.ms.uaa.repository.RoleRepository;
import com.portalasig.ms.uaa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class responsible for managing user operations such as registration, password updates, email settings, and
 * identity-based queries.
 * <p>
 * Also integrates with external notification systems for password recovery flows, and implements
 * {@link UserDetailsService} to support Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    public static final Set<UserRole> defaultRoleTypes = Set.of(UserRole.STUDENT);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Qualifier("clientCredentialsEmailClientV1")
    private final EmailOperations emailOperations;

    @Value("${ms.uaa.tools.users.default-password}")
    private final String defaultPassword;

    @Value("${portalasig.fe.url}")
    private final String frontEndUrl;
    private final TokenCreatorService tokenCreatorService;
    private final JwtDecoder jwtDecoder;

    /**
     * Loads a user by identity number for authentication.
     *
     * @param username
     *         the identity string
     * @return the authenticated user details
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByIdentity(Long.parseLong(username)).map(user -> {
            Set<RoleEntity> userRoles = user.getRoles();
            List<SimpleGrantedAuthority> authorities =
                    userRoles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getCode()))
                            .toList();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Registers a new user with the default role {@link UserRole#STUDENT}.
     *
     * @param request
     *         the registration request
     * @return the created user DTO
     * @throws ConflictException
     *         if the user identity already exists
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!userRepository.existsByIdentity(request.getIdentity())) {
            UserEntity userEntity = createUserToRegister(request);
            log.info(
                    "Registering user: first_name={} last_name={} identity={}",
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getIdentity()
            );
            userEntity = userRepository.save(userEntity);
            return userMapper.toDto(userEntity);
        } else {
            throw new ConflictException("User already exists");
        }
    }

    /**
     * Bulk register users.
     *
     * @param request
     *         the bulk registration request
     * @return list of registered users
     */
    public List<User> bulkRegister(List<RegisterRequest> request) {
        List<User> users = new ArrayList<>();
        List<UserEntity> newUserEntities = new ArrayList<>();
        request.forEach(registerRequest -> {
            Optional<UserEntity> optionalUserEntity = userRepository.findByIdentity(registerRequest.getIdentity());
            if (optionalUserEntity.isPresent()) {
                users.add(userMapper.toDto(optionalUserEntity.get()));
            } else {
                UserEntity userEntity = createUserToRegister(registerRequest);
                newUserEntities.add(userEntity);
            }
        });
        if (!newUserEntities.isEmpty()) {
            List<Long> identities = newUserEntities.stream().map(UserEntity::getIdentity).toList();
            log.info("Registering new identities={}", identities);
            List<UserEntity> userEntities = userRepository.saveAll(newUserEntities);
            List<User> newUsers = userEntities.stream().map(userMapper::toDto).toList();
            users.addAll(newUsers);
        }
        return users;
    }

    /**
     * Creates a user entity to register from a register request.
     *
     * @param request
     *         the user register request
     * @return a user entity ready to be persisted
     */
    public UserEntity createUserToRegister(RegisterRequest request) {
        Set<RoleEntity> defaultRoles = roleRepository.findAllByRoleIn(defaultRoleTypes);
        String username = request.getUsername() == null ?
                request.getIdentity().toString() :
                request.getUsername();
        String password = request.getPassword() == null ? defaultPassword : request.getPassword();

        return UserEntity
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identity(request.getIdentity())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .username(username)
                .roles(defaultRoles)
                .emailSettings(EmailSetting.defaultEmailSettings())
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    /**
     * Retrieves a user by their identity number.
     *
     * @param identity
     *         the user identity
     * @return the corresponding user DTO
     * @throws ResourceNotFoundException
     *         if the user is not found
     */
    public User getUserByIdentity(Long identity) {
        log.debug("Find user by identity: {}", identity);
        UserEntity user = userRepository.findByIdentity(identity)
                .orElse(null);
        return user == null ? null : userMapper.toDto(user);
    }

    /**
     * Changes the user's password, given their identity.
     *
     * @param identity
     *         the user identity
     * @param request
     *         the password update request
     * @throws ResourceNotFoundException
     *         if the user is not found
     */
    @Transactional
    @PreAuthorize("@userAuthorizer.isOwner(#identity)")
    public void changeUserPassword(Long identity, UserEditPasswordRequest request) {
        UserEntity existingUser = userRepository.findByIdentity(identity).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with user_id=%s not found", identity))
        );
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(existingUser);
        log.info("Password successfully edited for user_id={}", identity);
    }

    /**
     * Updates a user's email notification preferences.
     *
     * @param identity
     *         the user identity
     * @param request
     *         the email setting request
     * @return the updated user DTO
     * @throws ResourceNotFoundException
     *         if the user is not found
     */
    @Transactional
    @PreAuthorize("@userAuthorizer.isOwner(#identity)")
    public User updateEmailSettings(Long identity, EmailSettingRequest request) {
        UserEntity userEntity = userRepository.findByIdentity(identity).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with user_id=%s not found", identity))
        );
        List<String> emailSettings = request.getEmailSettings().stream().map(EmailSetting::getCode).toList();
        String emailSettingsString = null;
        if (!emailSettings.isEmpty()) {
            emailSettingsString = String.join(",", emailSettings);
        }
        userEntity.setEmailSettings(emailSettingsString);
        userEntity = userRepository.save(userEntity);

        return userMapper.toDto(userEntity);
    }

    /**
     * Updates the user's email address, if it's not already used.
     *
     * @param identity
     *         the user identity
     * @param request
     *         the new email address
     * @return the updated user DTO
     * @throws ConflictException
     *         if the email already exists
     * @throws ResourceNotFoundException
     *         if the user is not found
     */
    @Transactional
    @PreAuthorize("@userAuthorizer.isOwner(#identity)")
    public User updateEmailAddress(Long identity, EmailAddressRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (userEntity != null) {
            throw new ConflictException("Email already exists");
        }
        userEntity = userRepository.findByIdentity(identity).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with user_id=%s not found", identity))
        );
        userEntity.setEmail(request.getEmail());
        userEntity = userRepository.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    /**
     * Sends a password recovery email to the user with a temporary recovery token.
     *
     * @param identity
     *         the user identity
     * @throws ResourceNotFoundException
     *         if the user is not found
     */
    public void requestPasswordRecoveryToken(Long identity) {
        UserEntity userEntity = userRepository.findByIdentity(identity).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with user_id=%s not found", identity))
        );

        String passwordResetToken = tokenCreatorService.createPasswordResetToken(userEntity.getIdentity());
        String url = String.format("%s%s?token=%s", frontEndUrl, RestPaths.FrontEnd.RESET_PASSWORD, passwordResetToken);
        PasswordRecoveryTemplate passwordRecoveryTemplate = PasswordRecoveryTemplate
                .builder()
                .title("Nos llegó una solicitud para recuperar tu contraseña, ¿Fuiste tú?")
                .target(String.format(
                        "Hola, %s. Sigue los pasos a continuación para recuperar tu contraseña:",
                        userEntity.getFirstName()))
                .primaryBody("Si no lo solicitaste, por favor ignora este mensaje.")
                .secondaryBody("Ingrese al portal a través del siguiente enlace para recuperar su contraseña:")
                .url(url)
                .urlLabel("Recuperar contraseña")
                .closingMessage("Por su seguridad, el enlace vencerá en 5 minutos.")
                .build();

        String subject = String.format("¡Hola, %s! ¿Solicitaste recuperar tu contraseña?", userEntity.getFirstName());
        log.info("Sending password recovery email to subject={}", userEntity.getEmail());
        emailOperations.sendEmail(EmailRequest
                .builder()
                .emailTo(userEntity.getEmail())
                .subject(subject)
                .template(EmailTemplate.APP_NOTIFICATION)
                .templateConfiguration(passwordRecoveryTemplate)
                .build()
        );
    }

    /**
     * Resets the user's password using a valid password recovery token.
     *
     * @param request
     *         contains the recovery token and new password
     * @throws SystemErrorException
     *         if the token is invalid or user not found
     */
    public void resetUserPassword(UserRestorePasswordRequest request) {
        Jwt recoveryToken = decodeRecoveryToken(request.getRecoveryToken());
        Long identity = getIdentityFromToken(recoveryToken);
        UserEntity userEntity = userRepository.findByIdentity(identity)
                .orElseThrow(
                        () -> new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token")
                );
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userEntity);
    }

    @SuppressWarnings("AvoidCatchingGenericException")
    private Long getIdentityFromToken(Jwt recoveryToken) {
        try {
            return Long.valueOf(recoveryToken.getSubject());
        } catch (Exception e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
        }
    }

    private Jwt decodeRecoveryToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid recovery token");
        }
    }
}
