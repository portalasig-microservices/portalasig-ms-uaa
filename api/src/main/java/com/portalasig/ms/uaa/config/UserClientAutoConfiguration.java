package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.operation.UserOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration class for providing {@link UserOperations} WebClient proxies. Supports both client credentials and JWT
 * token relay authentication strategies.
 */
@Configuration
public class UserClientAutoConfiguration {

    /**
     * Creates a {@link UserOperations} proxy using a WebClient configured with client credentials.
     *
     * @param baseUrl
     *         the base URL of the UAA microservice
     * @param webClient
     *         the WebClient bean configured for client credentials
     * @return a proxy implementation of UserOperations
     */
    @Bean(name = "clientCredentialsUserClientV1")
    public UserOperations clientCredentialsUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("clientCredentialsWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(UserOperations.class);
    }

    /**
     * Creates a {@link UserOperations} proxy using a WebClient configured to relay JWT tokens.
     *
     * @param baseUrl
     *         the base URL of the UAA microservice
     * @param webClient
     *         the WebClient bean configured for JWT token relay
     * @return a proxy implementation of UserOperations
     */
    @Bean(name = "tokenRelayUserClientV1")
    public UserOperations tokenRelayUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("jwtTokenWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(UserOperations.class);
    }
}
