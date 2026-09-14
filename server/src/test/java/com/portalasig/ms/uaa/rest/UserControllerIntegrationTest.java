package com.portalasig.ms.uaa.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class UserControllerIntegrationTest extends AbstractMysqlIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void registerShouldCreateAndReturnUser() throws Exception {
    String body =
        "{\"identity\": 55443322, \"email\": \"controller-register@portalasig.ucv.ve\","
            + " \"password\": \"secret\", \"first_name\": \"Controller\", \"last_name\": \"Register\"}";

    mockMvc
        .perform(
            post("/v1/user/register")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.identity").value(55443322))
        .andExpect(jsonPath("$.email").value("controller-register@portalasig.ucv.ve"))
        .andExpect(jsonPath("$.roles[0]").value("STUDENT"));
  }

  @Test
  void registerWithoutAuthenticationShouldReturnUnauthorizedOrRedirect() throws Exception {
    mockMvc
        .perform(
            post("/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identity\": 1, \"email\": \"x@x.com\", \"password\": \"x\"}"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void getUserByIdentityShouldReturnSeededAdmin() throws Exception {
    mockMvc
        .perform(get("/v1/user/12345678").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("admin@portalasig.ucv.ve"))
        .andExpect(jsonPath("$.first_name").value("Administrador"));
  }
}
