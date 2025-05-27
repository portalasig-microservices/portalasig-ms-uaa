package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.operation.AdminUserOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration class for creating WebClient-based proxies for {@link AdminUserOperations}.
 * Provides two variants: one using client credentials and another using JWT token relay.
 */
@Configuration
public class AdminUserClientAutoConfiguration {

    /**
     * Creates an {@link AdminUserOperations} proxy using a WebClient configured with client credentials.
     *
     * @param baseUrl   the base URL of the UAA microservice
     * @param webClient a WebClient pre-configured for client credentials grant type
     * @return an AdminUserOperations implementation proxy
     */
    @Bean(name = "clientCredentialsAdminUserClientV1")
    public AdminUserOperations clientCredentialsAdminUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("clientCredentialsWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(AdminUserOperations.class);
    }

    /**
     * Creates an {@link AdminUserOperations} proxy using a WebClient configured for JWT token relay.
     *
     * @param baseUrl   the base URL of the UAA microservice
     * @param webClient a WebClient pre-configured to propagate user JWT tokens
     * @return an AdminUserOperations implementation proxy
     */
    @Bean(name = "tokenRelayAdminUserClientV1")
    public AdminUserOperations tokenRelayAdminUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("jwtTokenWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(AdminUserOperations.class);
    }
}
