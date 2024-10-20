package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import com.portalasig.ms.uaa.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseClientRegistrationRepository implements ClientRegistrationRepository {

    private final ClientRepository clientRepository;

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        ClientEntity clientEntity = clientRepository.findByClientId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + registrationId));

        return ClientRegistration.withRegistrationId(registrationId).clientId(clientEntity.getClientId())
                .clientSecret(clientEntity.getSecret())
                .authorizationGrantType(new AuthorizationGrantType(clientEntity.getGrantTypes()))
                .redirectUri(clientEntity.getRedirectUri()).scope(clientEntity.getScopes()) // Assuming
                // scopes are
                // comma-separated
                .build();
    }
}
