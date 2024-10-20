package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.client.UserAuthenticationClient;
import com.portalasig.ms.uaa.client.UserAuthenticationClientV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UaaClientAutoConfiguration {

    @Primary
    @Bean(name = "userAuthenticationClientV1")
    public UserAuthenticationClient getUserAuthenticationClientV1(
            @Value("${portalasig.uaa.ms.v1.url}") String baseUrl, WebClient webClient) {
        return new UserAuthenticationClientV1(webClient, baseUrl);
    }

    @Bean
    ReactiveClientRegistrationRepository clientRegistrations(
            @Value("${spring.security.oauth2.client.provider.portalasig_engine.token-uri}")
            String token_uri,
            @Value("${spring.security.oauth2.client.registration.portalasig_engine.client-id}")
            String client_id,
            @Value("${spring.security.oauth2.client.registration.portalasig_engine.client-secret}")
            String client_secret,
            @Value(
                    "${spring.security.oauth2.client.registration.portalasig_engine.authorization-grant-type}")
            String authorizationGrantType) {

        ClientRegistration registration =
                ClientRegistration.withRegistrationId("portalasig_engine")
                        .tokenUri(token_uri)
                        .clientId(client_id)
                        .clientSecret(client_secret)
                        .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                        .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    public AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        InMemoryReactiveOAuth2AuthorizedClientService clientService =
                new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean("webClient")
    WebClient webClient(
            AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager oauth2AuthorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(oauth2AuthorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("portalasig_engine");
        return WebClient.builder().filter(oauth2Client).build();
    }
}
