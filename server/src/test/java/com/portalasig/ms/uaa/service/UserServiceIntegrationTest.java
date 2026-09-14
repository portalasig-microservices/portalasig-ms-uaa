package com.portalasig.ms.uaa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.portalasig.ms.commons.rest.exception.ConflictException;
import com.portalasig.ms.commons.rest.exception.ResourceNotFoundException;
import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.EmailAddressRequest;
import com.portalasig.ms.uaa.dto.EmailSettingRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserEditPasswordRequest;
import com.portalasig.ms.uaa.dto.UserRestorePasswordRequest;
import com.portalasig.ms.uaa.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class UserServiceIntegrationTest extends AbstractMysqlIntegrationTest {

  private static final Long SEEDED_ADMIN_IDENTITY = 12345678L;
  private static final Long NEW_USER_IDENTITY = 99887766L;

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private TokenCreatorService tokenCreatorService;

  private static RegisterRequest baseRegisterRequest(Long identity) {
    return RegisterRequest.builder()
        .identity(identity)
        .email(String.format("user%s@portalasig.ucv.ve", identity))
        .password("plain-password")
        .firstName("Integration")
        .lastName("Test")
        .build();
  }

  @Test
  void registerUserShouldPersistUserWithStudentRoleAndEncodedPassword() {
    User user = userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));

    assertThat(user.getUserId()).isNotNull();
    assertThat(user.getIdentity()).isEqualTo(NEW_USER_IDENTITY);
    assertThat(user.getRoles()).containsExactly(UserRole.STUDENT);

    UserEntity persisted = userRepository.findByIdentity(NEW_USER_IDENTITY).orElseThrow();
    // username defaults to the identity when not provided in the request
    assertThat(persisted.getUsername()).isEqualTo(NEW_USER_IDENTITY.toString());
    assertThat(persisted.getPassword()).isNotEqualTo("plain-password");
    assertThat(passwordEncoder.matches("plain-password", persisted.getPassword())).isTrue();
  }

  @Test
  void registerUserWithExistingIdentityShouldThrowConflict() {
    RegisterRequest request = baseRegisterRequest(SEEDED_ADMIN_IDENTITY);

    assertThatThrownBy(() -> userService.registerUser(request))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void bulkRegisterShouldCreateOnlyNewUsersAndReturnExistingOnes() {
    List<RegisterRequest> requests =
        List.of(baseRegisterRequest(SEEDED_ADMIN_IDENTITY), baseRegisterRequest(NEW_USER_IDENTITY));

    List<User> users = userService.bulkRegister(requests);

    assertThat(users).hasSize(2);
    assertThat(users.stream().map(User::getIdentity))
        .containsExactlyInAnyOrder(SEEDED_ADMIN_IDENTITY, NEW_USER_IDENTITY);
    assertThat(userRepository.existsByIdentity(NEW_USER_IDENTITY)).isTrue();
  }

  @Test
  void getUserByIdentityShouldReturnSeededAdminUser() {
    User user = userService.getUserByIdentity(SEEDED_ADMIN_IDENTITY);

    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo("admin@portalasig.ucv.ve");
    assertThat(user.getRoles()).contains(UserRole.ADMIN);
  }

  @Test
  void loadUserByUsernameShouldExposeRolesAsAuthorities() {
    UserDetails details = userService.loadUserByUsername(SEEDED_ADMIN_IDENTITY.toString());

    assertThat(details.getUsername()).isEqualTo("admin@portalasig.ucv.ve");
    assertThat(details.getAuthorities())
        .anySatisfy(authority -> assertThat(authority.getAuthority()).isEqualTo("ADMIN"));
  }

  @Test
  void loadUserByUsernameWithUnknownIdentityShouldThrowNotFound() {
    assertThatThrownBy(() -> userService.loadUserByUsername("999999999"))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updateEmailSettingsShouldPersistJoinedSettingCodes() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    authenticateAs(NEW_USER_IDENTITY, "STUDENT");
    EmailSettingRequest request =
        EmailSettingRequest.builder()
            .emailSettings(List.of(EmailSetting.NEWS, EmailSetting.FORUM))
            .build();

    User user = userService.updateEmailSettings(NEW_USER_IDENTITY, request);

    assertThat(user.getEmailSettings()).containsExactly(EmailSetting.NEWS, EmailSetting.FORUM);
    UserEntity persisted = userRepository.findByIdentity(NEW_USER_IDENTITY).orElseThrow();
    assertThat(persisted.getEmailSettings()).isEqualTo("NEWS,FORUM");
  }

  @Test
  void updateEmailSettingsWithEmptyListShouldStoreEmptyCode() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    authenticateAs(NEW_USER_IDENTITY, "STUDENT");

    userService.updateEmailSettings(
        NEW_USER_IDENTITY, EmailSettingRequest.builder().emailSettings(List.of()).build());

    UserEntity persisted = userRepository.findByIdentity(NEW_USER_IDENTITY).orElseThrow();
    assertThat(persisted.getEmailSettings()).isEmpty();
  }

  @Test
  void updateEmailAddressWithUnusedEmailShouldSucceed() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    authenticateAs(NEW_USER_IDENTITY, "STUDENT");

    User user =
        userService.updateEmailAddress(
            NEW_USER_IDENTITY,
            EmailAddressRequest.builder().email("new-email@portalasig.ucv.ve").build());

    assertThat(user.getEmail()).isEqualTo("new-email@portalasig.ucv.ve");
  }

  @Test
  void updateEmailAddressWithTakenEmailShouldThrowConflict() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    authenticateAs(NEW_USER_IDENTITY, "STUDENT");
    EmailAddressRequest request =
        EmailAddressRequest.builder().email("admin@portalasig.ucv.ve").build();

    assertThatThrownBy(() -> userService.updateEmailAddress(NEW_USER_IDENTITY, request))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void changeUserPasswordAsOwnerShouldUpdatePassword() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    authenticateAs(NEW_USER_IDENTITY, "STUDENT");

    userService.changeUserPassword(
        NEW_USER_IDENTITY, UserEditPasswordRequest.builder().password("changed-password").build());

    UserEntity persisted = userRepository.findByIdentity(NEW_USER_IDENTITY).orElseThrow();
    assertThat(passwordEncoder.matches("changed-password", persisted.getPassword())).isTrue();
  }

  @Test
  void requestPasswordRecoveryTokenShouldBuildTemplateWithoutErrors() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));

    org.assertj.core.api.Assertions.assertThatNoException()
        .isThrownBy(() -> userService.requestPasswordRecoveryToken(NEW_USER_IDENTITY));
  }

  @Test
  void requestPasswordRecoveryTokenWithUnknownUserShouldThrowNotFound() {
    assertThatThrownBy(() -> userService.requestPasswordRecoveryToken(999999999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  private static void authenticateAs(Long identity, String... authorities) {
    Jwt jwt =
        Jwt.withTokenValue("test-token")
            .header("alg", "RS256")
            .claim("username", identity.toString())
            .build();
    List<SimpleGrantedAuthority> granted =
        Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
    SecurityContextHolder.getContext()
        .setAuthentication(new JwtAuthenticationToken(jwt, granted));
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void resetUserPasswordWithValidRecoveryTokenShouldChangePassword() {
    userService.registerUser(baseRegisterRequest(NEW_USER_IDENTITY));
    String recoveryToken = tokenCreatorService.createPasswordResetToken(NEW_USER_IDENTITY);
    UserRestorePasswordRequest request =
        UserRestorePasswordRequest.builder()
            .recoveryToken(recoveryToken)
            .password("brand-new-password")
            .build();

    userService.resetUserPassword(request);

    UserEntity persisted = userRepository.findByIdentity(NEW_USER_IDENTITY).orElseThrow();
    assertThat(passwordEncoder.matches("brand-new-password", persisted.getPassword())).isTrue();
  }
}
