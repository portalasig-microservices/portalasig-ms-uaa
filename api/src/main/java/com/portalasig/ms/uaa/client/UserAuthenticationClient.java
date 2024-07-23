package com.portalasig.ms.uaa.client;

import com.portalasig.ms.uaa.dto.User;
import reactor.core.publisher.Mono;

public interface UserAuthenticationClient {

    /**
     * Find user by identity.
     *
     * @param identity
     *         the identity
     * @return the user wrapped around a Mono object
     */
    Mono<User> findUserByIdentity(Long identity);
}
