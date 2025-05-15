package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.operation.AdminUserOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AdminUserClientAutoConfiguration {

    @Bean(name = "clientCredentialsAdminUserClientV1")
    public AdminUserOperations clientCredentialsAdminUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("clientCredentialsWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(AdminUserOperations.class);
    }

    @Bean(name = "tokenRelayAdminUserClientV1")
    public AdminUserOperations tokenRelayAdminUserClientV1(
            @Value("${portalasig.uaa.ms.url}") String baseUrl,
            @Qualifier("jwtTokenWebClient") WebClient webClient) {
        WebClient client = webClient.mutate().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.create(client)).build();
        return factory.createClient(AdminUserOperations.class);
    }
}
