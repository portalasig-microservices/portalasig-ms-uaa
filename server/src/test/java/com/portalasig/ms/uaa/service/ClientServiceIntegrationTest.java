package com.portalasig.ms.uaa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.portalasig.ms.commons.test.AbstractMysqlIntegrationTest;
import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import com.portalasig.ms.uaa.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

class ClientServiceIntegrationTest extends AbstractMysqlIntegrationTest {

  @Autowired private ClientService clientService;

  @Autowired private ClientRepository clientRepository;

  @Test
  void findByClientIdShouldReturnSeededClientWithGrantTypesAndScopes() {
    ClientEntity seeded = clientRepository.findAll().stream().findFirst().orElseThrow();

    RegisteredClient client = clientService.findByClientId(seeded.getClientId());

    assertThat(client.getClientId()).isEqualTo(seeded.getClientId());
    assertThat(client.getClientSecret()).isEqualTo(seeded.getSecret());
    assertThat(client.getAuthorizationGrantTypes()).isNotEmpty();
    assertThat(client.getScopes()).isNotEmpty();
  }

  @Test
  void findByClientIdWithUnknownClientShouldThrowBadCredentials() {
    assertThatThrownBy(() -> clientService.findByClientId("unknown-client"))
        .isInstanceOf(BadCredentialsException.class);
  }
}
