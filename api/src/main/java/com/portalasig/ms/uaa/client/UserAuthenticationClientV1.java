package com.portalasig.ms.uaa.client;

import com.portalasig.ms.uaa.constant.UserPaths;
import com.portalasig.ms.uaa.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserAuthenticationClientV1 implements UserAuthenticationClient {

    private final WebClient webClient;
    private final String baseUrl;

    @Override
    public Mono<User> findUserByIdentity(Long identity) {
        return webClient.get()
                .uri(baseUrl + UserPaths.USER_PATH + UserPaths.IDENTITY, identity)
                .retrieve()
                .bodyToMono(User.class);
    }
}
