package com.portalasig.ms.uaa.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest extends AbstractMysqlIntegrationTest {

  private static final Long IDENTITY = 66554433L;
  private static final String PASSWORD = "controller-password";

  @Autowired private MockMvc mockMvc;

  @Autowired private UserService userService;

  @BeforeEach
  void registerUser() {
    userService.registerUser(
        RegisterRequest.builder()
            .identity(IDENTITY)
            .email("auth-controller@portalasig.ucv.ve")
            .password(PASSWORD)
            .firstName("Auth")
            .lastName("Controller")
            .build());
  }

  @Test
  void loginWithValidCredentialsShouldReturnTokenPair() throws Exception {
    String body =
        String.format("{\"username\": \"%s\", \"password\": \"%s\"}", IDENTITY, PASSWORD);

    mockMvc
        .perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").isNotEmpty())
        .andExpect(jsonPath("$.refresh_token").isNotEmpty())
        .andExpect(jsonPath("$.username").value(IDENTITY.toString()));
  }

  @Test
  void loginWithInvalidPasswordShouldReturnUnauthorized() throws Exception {
    String body = String.format("{\"username\": \"%s\", \"password\": \"wrong\"}", IDENTITY);

    mockMvc
        .perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void refreshTokenShouldReturnNewAccessToken() throws Exception {
    String loginBody =
        String.format("{\"username\": \"%s\", \"password\": \"%s\"}", IDENTITY, PASSWORD);
    String loginResponse =
        mockMvc
            .perform(
                post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String refreshToken =
        com.jayway.jsonpath.JsonPath.read(loginResponse, "$.refresh_token");

    String refreshBody = String.format("{\"refresh_token\": \"%s\"}", refreshToken);
    mockMvc
        .perform(
            post("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").isNotEmpty());
  }

  @Test
  void refreshWithGarbageTokenShouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            post("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refresh_token\": \"garbage\"}"))
        .andExpect(status().isUnauthorized());
  }
}
