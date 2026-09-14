package com.portalasig.ms.uaa.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class AdminUserServiceIntegrationTest extends AbstractMysqlIntegrationTest {

  private static final Long IDENTITY = 77665544L;

  @Autowired private AdminUserService adminUserService;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUpAdminSecurityContext() {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            "admin", null, List.of(new SimpleGrantedAuthority("ADMIN")));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void findAllShouldReturnPaginatedSeededUsers() {
    Paginated<User> result =
        adminUserService.findAll(false, false, PageRequest.of(0, 10));

    // findAll only exposes STUDENT and PROFESSOR users; the seeded ADMIN is not listed
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent())
        .allSatisfy(
            user ->
                assertThat(user.getRoles())
                    .anyMatch(role -> role == UserRole.STUDENT || role == UserRole.PROFESSOR));
  }

  @Test
  void findAllStudentsOnlyShouldFilterUsersByRole() {
    Paginated<User> result = adminUserService.findAll(true, false, PageRequest.of(0, 50));

    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent())
        .allSatisfy(user -> assertThat(user.getRoles()).contains(UserRole.STUDENT));
  }

  @Test
  void upsertUserShouldCreateNewUserWithRequestedRole() {
    UserRequest request =
        UserRequest.builder()
            .identity(IDENTITY)
            .email("upsert@portalasig.ucv.ve")
            .firstName("Upsert")
            .lastName("User")
            .userRole(UserRole.PROFESSOR)
            .build();

    User user = adminUserService.upsertUser(request);

    assertThat(user.getIdentity()).isEqualTo(IDENTITY);
    assertThat(user.getRoles()).contains(UserRole.PROFESSOR);
    assertThat(userRepository.existsByIdentity(IDENTITY)).isTrue();
  }

  @Test
  void upsertUserShouldUpdateExistingUserNames() {
    UserRequest create =
        UserRequest.builder()
            .identity(IDENTITY)
            .email("upsert-update@portalasig.ucv.ve")
            .firstName("Original")
            .lastName("Name")
            .userRole(UserRole.STUDENT)
            .build();
    adminUserService.upsertUser(create);

    UserRequest update =
        UserRequest.builder()
            .identity(IDENTITY)
            .email("upsert-update@portalasig.ucv.ve")
            .firstName("Updated")
            .lastName("Name")
            .userRole(UserRole.STUDENT)
            .build();
    User updated = adminUserService.upsertUser(update);

    assertThat(updated.getFirstName()).isEqualTo("Updated");
  }

  @Test
  void deleteUserShouldRemoveUserFromRepository() {
    adminUserService.upsertUser(
        UserRequest.builder()
            .identity(IDENTITY)
            .email("to-delete@portalasig.ucv.ve")
            .firstName("Delete")
            .lastName("Me")
            .userRole(UserRole.STUDENT)
            .build());

    adminUserService.deleteUser(IDENTITY);

    assertThat(userRepository.existsByIdentity(IDENTITY)).isFalse();
  }

  @Test
  void createUsersFromCsvShouldImportAllRows() {
    String csv =
        "identity,name,lastname,email,active,created_at,updated_at,role,email_settings\n"
            + "11111111,John,Doe,john.doe@portalasig.ucv.ve,true,2024-01-01 00:00:00.000000 Z,2024-01-01 00:00:00.000000 Z,STUDENT,1\n"
            + "22222222,Jane,Doe,jane.doe@portalasig.ucv.ve,true,2024-01-01 00:00:00.000000 Z,2024-01-01 00:00:00.000000 Z,PROFESSOR,1\n";

    adminUserService.createUsersFromCsv(
        new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

    assertThat(userRepository.existsByIdentity(11111111L)).isTrue();
    assertThat(userRepository.existsByIdentity(22222222L)).isTrue();
  }
}
