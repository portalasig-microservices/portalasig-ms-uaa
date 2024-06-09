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

@Service
@AllArgsConstructor
public class ClientService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Optional<ClientEntity> clientOptional = clientRepository.findByClientId(clientId);

        return clientOptional.map(client -> {
            List<AuthorizationGrantType> authorizationGrantTypes = Arrays
                    .stream(client.getGrantTypes().split(","))
                    .map(AuthorizationGrantType::new).toList();
            List<ClientAuthenticationMethod> clientAuthenticationMethods = Arrays
                    .stream(client.getAuthenticationMethods().split(","))
                    .map(ClientAuthenticationMethod::new).toList();
            List<String> scopes = Arrays.stream(client.getScopes().split(",")).toList();
            return RegisteredClient
                    .withId(client.getId().toString())
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

    private TokenSettings tokenSettings() {
        return TokenSettings
                .builder()
                .accessTokenTimeToLive(Duration.ofHours(2))
                .build();
    }
}
