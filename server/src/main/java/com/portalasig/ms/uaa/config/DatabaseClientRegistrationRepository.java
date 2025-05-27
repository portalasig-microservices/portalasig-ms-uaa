package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import com.portalasig.ms.uaa.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of {@link ClientRegistrationRepository} that retrieves OAuth2 client registration details from
 * a database using a {@link ClientRepository}.
 */
@Component
@RequiredArgsConstructor
public class DatabaseClientRegistrationRepository implements ClientRegistrationRepository {

    private final ClientRepository clientRepository;

    /**
     * Loads a {@link ClientRegistration} from the database using the provided registration ID.
     *
     * @param registrationId
     *         the registration ID of the client
     * @return the corresponding {@link ClientRegistration}
     * @throws IllegalArgumentException
     *         if no client is found with the given registration ID
     */
    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        ClientEntity clientEntity = clientRepository.findByClientId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + registrationId));

        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(clientEntity.getClientId())
                .clientSecret(clientEntity.getSecret())
                .authorizationGrantType(new AuthorizationGrantType(clientEntity.getGrantTypes()))
                .redirectUri(clientEntity.getRedirectUri())
                .scope(clientEntity.getScopes()) // Assumes comma-separated string
                .build();
    }
}
