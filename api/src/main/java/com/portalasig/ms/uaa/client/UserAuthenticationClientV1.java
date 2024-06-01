package com.portalasig.ms.uaa.client;

import com.portalasig.ms.uaa.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserAuthenticationClientV1 implements UserAuthenticationClient {

    private final WebClient webClient = WebClient.create();
    private final String baseUrl;

    @Override
    public Mono<User> findUserByUsername(String username) {
        return webClient.get()
            .uri(baseUrl + "/user/{username}", username)
            .retrieve()
            .bodyToMono(User.class);
    }
}
