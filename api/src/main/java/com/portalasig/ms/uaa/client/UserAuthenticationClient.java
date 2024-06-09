package com.portalasig.ms.uaa.client;

import com.portalasig.ms.uaa.dto.User;
import reactor.core.publisher.Mono;

public interface UserAuthenticationClient {

    /**
     * Find user by username
     *
     * @param username the username
     * @return the user object
     */
    Mono<User> findUserByUsername(String username);
}
