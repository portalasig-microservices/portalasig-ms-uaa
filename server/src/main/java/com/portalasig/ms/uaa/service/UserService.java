package com.portalasig.ms.uaa.service;

import com.portalasig.ms.commons.rest.exception.ConflictException;
import com.portalasig.ms.commons.rest.exception.ResourceNotFoundException;
import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.notify.client.EmailNotifyClient;
import com.portalasig.ms.notify.constant.EmailTemplate;
import com.portalasig.ms.notify.dto.EmailRequest;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    public static final Set<UserRole> defaultRoleTypes = Set.of(UserRole.STUDENT);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Qualifier("emailNotifyClientV1")
    private final EmailNotifyClient emailNotifyClient;

    @Value("${portalasig.fe.url}")
    private final String frontEndUrl;
    private final TokenCreatorService tokenCreatorService;
    private final JwtDecoder jwtDecoder;

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

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!userRepository.existsByIdentity(request.getIdentity())) {
            Set<RoleEntity> roleEntities = defaultRoleTypes.stream().map(role -> RoleEntity
                    .builder()
                    .role(role)
                    .build()).collect(Collectors.toSet());
            String username = request.getUsername() == null ?
                    request.getIdentity().toString() :
                    request.getUsername();

            UserEntity userEntity = UserEntity
                    .builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .identity(request.getIdentity())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .username(username)
                    .roles(roleEntities)
                    .emailSettings(EmailSetting.defaultEmailSettings())
                    .createdDate(Instant.now())
                    .updatedDate(Instant.now())
                    .build();

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

    @PreAuthorize("@userAuthorizer.isOwner(#identity)")
    public User getUserByIdentity(Long identity) {
        log.debug("Find user by identity: {}", identity);
        UserEntity user = userRepository.findByIdentity(identity)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %s not found", identity)));
        return userMapper.toDto(user);
    }

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

        emailNotifyClient.sendApplicationEmail(EmailRequest
                        .builder()
                        .emailTo(userEntity.getEmail())
                        .subject(String.format("¡Hola, %s! ¿Solicitaste recuperar tu contraseña?", userEntity.getFirstName()))
                        .template(EmailTemplate.APP_NOTIFICATION)
                        .templateConfiguration(passwordRecoveryTemplate)
                        .build()
                )
                .doOnSuccess(response -> log.info("Password recovery email sent to {}", userEntity.getEmail()))
                .doOnError(error -> log.error(error.getMessage(), error))
                .subscribe();
    }

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
