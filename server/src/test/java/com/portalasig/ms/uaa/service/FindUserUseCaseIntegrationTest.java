package com.portalasig.ms.uaa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.dto.User;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class FindUserUseCaseIntegrationTest extends AbstractMysqlIntegrationTest {

  @Autowired private FindUserUseCase findUserUseCase;

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
  void findUsersByLastNameShouldMatchSeededUsers() {
    List<User> users = findUserUseCase.findUsers("prueba", null);

    assertThat(users).hasSize(2);
    assertThat(users)
        .anySatisfy(user -> assertThat(user.getEmail()).isEqualTo("profesor@portalasig.ucv.ve"))
        .anySatisfy(user -> assertThat(user.getEmail()).isEqualTo("estudiante@portalasig.ucv.ve"));
  }

  @Test
  void findUsersWithExplicitRoleShouldFilterResults() {
    List<User> users = findUserUseCase.findUsers("prueba", List.of(UserRole.STUDENT));

    assertThat(users).hasSize(1);
    assertThat(users.get(0).getRoles()).contains(UserRole.STUDENT);
  }

  @Test
  void findUsersWithEmptyRolesShouldDefaultToStudentsAndProfessors() {
    List<User> users = findUserUseCase.findUsers("prueba", List.of());

    assertThat(users).hasSize(2);
  }

  @Test
  void findUsersByIdentityShouldMatchExactlyOneUser() {
    List<User> users = findUserUseCase.findUsers("32345678", null);

    assertThat(users).hasSize(1);
    assertThat(users.get(0).getIdentity()).isEqualTo(32345678L);
  }

  @Test
  void findUsersWithRestrictedRoleShouldThrowBadRequest() {
    assertThatThrownBy(() -> findUserUseCase.findUsers("prueba", List.of(UserRole.ADMIN)))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void findUsersWithoutMatchesShouldReturnEmptyList() {
    List<User> users = findUserUseCase.findUsers("no-existe-este-usuario", null);

    assertThat(users).isEmpty();
  }
}
