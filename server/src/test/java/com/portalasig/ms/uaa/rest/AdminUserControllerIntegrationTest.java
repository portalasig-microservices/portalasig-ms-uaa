package com.portalasig.ms.uaa.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@AutoConfigureMockMvc
class AdminUserControllerIntegrationTest extends AbstractMysqlIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  private static RequestPostProcessor adminJwt() {
    return jwt().authorities(new SimpleGrantedAuthority("ADMIN"));
  }

  @Test
  void upsertUserShouldCreateUserThroughHttp() throws Exception {
    String body =
        "{\"identity\": 44332211, \"email\": \"admin-upsert@portalasig.ucv.ve\","
            + " \"first_name\": \"Admin\", \"last_name\": \"Upsert\", \"user_role\": \"PROFESSOR\"}";

    mockMvc
        .perform(
            post("/v1/user").with(adminJwt()).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.identity").value(44332211))
        .andExpect(jsonPath("$.roles[0]").value("PROFESSOR"));
  }

  @Test
  void upsertUserWithoutAdminAuthorityShouldReturnForbidden() throws Exception {
    mockMvc
        .perform(
            post("/v1/user")
                .with(jwt().authorities(new SimpleGrantedAuthority("STUDENT")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"identity\": 1, \"email\": \"x@x.com\", \"first_name\": \"x\","
                        + " \"last_name\": \"x\", \"user_role\": \"STUDENT\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void findUsersShouldReturnMatches() throws Exception {
    mockMvc
        .perform(get("/v1/user/find").with(adminJwt()).param("query", "prueba"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void deleteUserShouldRemoveIt() throws Exception {
    String body =
        "{\"identity\": 33221100, \"email\": \"admin-delete@portalasig.ucv.ve\","
            + " \"first_name\": \"Admin\", \"last_name\": \"Delete\", \"user_role\": \"STUDENT\"}";
    mockMvc
        .perform(
            post("/v1/user").with(adminJwt()).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk());

    mockMvc
        .perform(delete("/v1/user/33221100").with(adminJwt()))
        .andExpect(status().isOk());
    org.assertj.core.api.Assertions.assertThat(userRepository.existsByIdentity(33221100L))
        .isFalse();
  }

  @Test
  void importUsersFromCsvShouldCreateThem() throws Exception {
    String csv =
        "identity,name,lastname,email,active,created_at,updated_at,role,email_settings\n"
            + "55555555,Csv,User,csv.user@portalasig.ucv.ve,true,2024-01-01 00:00:00.000000 Z,2024-01-01 00:00:00.000000 Z,STUDENT,1\n";
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "users.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(multipart("/v1/user/_import").file(file).with(adminJwt()))
        .andExpect(status().isOk());
    org.assertj.core.api.Assertions.assertThat(userRepository.existsByIdentity(55555555L))
        .isTrue();
  }
}
