package com.portalasig.ms.uaa.config;

import com.portalasig.ms.uaa.client.UserAuthenticationClient;
import com.portalasig.ms.uaa.client.UserAuthenticationClientV1;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//@Configuration
//public class UaaClientAutoConfiguration {
//
//    @Primary
//    @Bean(name = "userAuthenticationClientV1")
//    public UserAuthenticationClient getUserAuthenticationClientV1(
//            @Value("${portalasig.uaa.ms.v1.url}") String baseUrl,
//            @Qualifier("tokenRelayOAuth2RestTemplate") OAuth2RestTemplate tokenRelayRestTemplate) {
//        return new UserAuthenticationClientV1(baseUrl);
//    }
//}
