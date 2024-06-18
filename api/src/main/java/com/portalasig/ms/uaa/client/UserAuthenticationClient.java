package com.portalasig.ms.uaa.client;

import com.portalasig.ms.uaa.dto.User;
import reactor.core.publisher.Mono;

public interface UserAuthenticationClient {

    Mono<User> findUserByUsername();
}
