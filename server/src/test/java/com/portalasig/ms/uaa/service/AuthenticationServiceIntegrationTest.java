package com.portalasig.ms.uaa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class AuthenticationServiceIntegrationTest extends AbstractMysqlIntegrationTest {

  private static final Long IDENTITY = 88776655L;
  private static final String PASSWORD = "integration-password";

  @Autowired private AuthenticationService authenticationService;

  @Autowired private UserService userService;

  @Autowired private TokenCreatorService tokenCreatorService;

  @Autowired private JwtDecoder jwtDecoder;

  @BeforeEach
  void registerUser() {
    userService.registerUser(
        RegisterRequest.builder()
            .identity(IDENTITY)
            .email("auth-test@portalasig.ucv.ve")
            .password(PASSWORD)
            .firstName("Auth")
            .lastName("Test")
            .build());
  }

  @Test
  void loginWithValidCredentialsShouldReturnAccessAndRefreshTokens() {
    ExchangeToken exchangeToken =
        authenticationService.loginAndGenerateTokens(
            LoginRequest.builder().username(IDENTITY.toString()).password(PASSWORD).build());

    assertThat(exchangeToken.getAccessToken()).isNotBlank();
    assertThat(exchangeToken.getRefreshToken()).isNotBlank();

    Jwt accessToken = jwtDecoder.decode(exchangeToken.getAccessToken());
    assertThat(accessToken.getClaimAsString("username")).isEqualTo(IDENTITY.toString());
    assertThat(accessToken.getExpiresAt())
        .isBefore(Instant.now().plus(1, ChronoUnit.HOURS).plusSeconds(60));
  }

  @Test
  void loginWithInvalidPasswordShouldThrowUnauthorized() {
    LoginRequest request =
        LoginRequest.builder().username(IDENTITY.toString()).password("wrong-password").build();

    assertThatThrownBy(() -> authenticationService.loginAndGenerateTokens(request))
        .isInstanceOf(SystemErrorException.class)
        .hasMessageContaining("Invalid username or password");
  }

  @Test
  void refreshAccessTokenShouldIssueANewValidAccessToken() {
    ExchangeToken initial =
        authenticationService.loginAndGenerateTokens(
            LoginRequest.builder().username(IDENTITY.toString()).password(PASSWORD).build());

    ExchangeToken refreshed =
        authenticationService.refreshAccessToken(
            RefreshTokenRequest.builder().refreshToken(initial.getRefreshToken()).build());

    // within the same second the re-issued token may be identical; what matters is its validity
    assertThat(refreshed.getAccessToken()).isNotBlank();
    assertThat(refreshed.getRefreshToken()).isEqualTo(initial.getRefreshToken());
    Jwt decoded = jwtDecoder.decode(refreshed.getAccessToken());
    assertThat(decoded.getExpiresAt()).isAfter(Instant.now());
  }

  @Test
  void refreshWithGarbageTokenShouldThrowUnauthorized() {
    RefreshTokenRequest request =
        RefreshTokenRequest.builder().refreshToken("not-a-jwt").build();

    assertThatThrownBy(() -> authenticationService.refreshAccessToken(request))
        .isInstanceOf(SystemErrorException.class);
  }

  @Test
  void passwordRecoveryTokenShouldBeAcceptedAsValid() {
    String recoveryToken = tokenCreatorService.createPasswordResetToken(IDENTITY);

    assertThat(authenticationService.isPasswordRecoveryTokenValid(recoveryToken)).isTrue();
  }

  @Test
  void accessTokenShouldBeRejectedAsRecoveryToken() {
    ExchangeToken exchangeToken =
        authenticationService.loginAndGenerateTokens(
            LoginRequest.builder().username(IDENTITY.toString()).password(PASSWORD).build());

    assertThatThrownBy(
            () -> authenticationService.isPasswordRecoveryTokenValid(exchangeToken.getAccessToken()))
        .isInstanceOf(SystemErrorException.class);
  }
}
