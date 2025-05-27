package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.domain.entity.ClientEntity;
import com.portalasig.ms.uaa.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link RegisteredClientRepository} to fetch OAuth2 client details from the database and map them to
 * Spring Security's {@link RegisteredClient}.
 */
@Service
@AllArgsConstructor
public class ClientService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    /**
     * Saves a new registered client.
     * <p>
     * This method is not implemented because clients are managed externally.
     *
     * @param registeredClient
     *         the client to save
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        // Not implemented
    }

    /**
     * Finds a registered client by its internal ID.
     * <p>
     * This method is not used in the current context.
     *
     * @param id
     *         the internal ID of the client
     * @return null (not implemented)
     */
    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    /**
     * Finds a registered client by its public client ID.
     *
     * @param clientId
     *         the client ID
     * @return the matching {@link RegisteredClient}
     * @throws BadCredentialsException
     *         if no client is found
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        Optional<ClientEntity> clientOptional = clientRepository.findByClientId(clientId);

        return clientOptional.map(client -> {
            List<AuthorizationGrantType> authorizationGrantTypes = Arrays
                    .stream(client.getGrantTypes().split(","))
                    .map(AuthorizationGrantType::new)
                    .toList();

            List<ClientAuthenticationMethod> clientAuthenticationMethods = Arrays
                    .stream(client.getAuthenticationMethods().split(","))
                    .map(ClientAuthenticationMethod::new)
                    .toList();

            List<String> scopes = Arrays
                    .stream(client.getScopes().split(","))
                    .toList();

            return RegisteredClient.withId(client.getId().toString())
                    .clientId(client.getClientId())
                    .clientSecret(client.getSecret())
                    .clientName(client.getName())
                    .redirectUri(client.getRedirectUri())
                    .postLogoutRedirectUri(client.getRedirectUriLogout())
                    .clientAuthenticationMethod(clientAuthenticationMethods.get(0))
                    .clientAuthenticationMethod(clientAuthenticationMethods.get(1))
                    .clientAuthenticationMethod(clientAuthenticationMethods.get(2))
                    .scope(scopes.get(0))
                    .scope(scopes.get(1))
                    .authorizationGrantType(authorizationGrantTypes.get(0))
                    .authorizationGrantType(authorizationGrantTypes.get(1))
                    .authorizationGrantType(authorizationGrantTypes.get(2))
                    .tokenSettings(tokenSettings())
                    .build();
        }).orElseThrow(() -> new BadCredentialsException("Client not found"));
    }

    /**
     * Returns the default token settings used for all clients.
     *
     * @return the {@link TokenSettings}
     */
    private TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(2))
                .build();
    }
}
