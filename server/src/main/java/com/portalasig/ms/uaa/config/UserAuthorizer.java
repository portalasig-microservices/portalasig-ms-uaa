package com.portalasig.ms.uaa.config;

import com.portalasig.ms.commons.constants.Authority;
import com.portalasig.ms.commons.rest.security.CurrentAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Authorization utility to check whether the current user is allowed to access or modify resources related to a
 * specific identity.
 */
@Component
@RequiredArgsConstructor
public class UserAuthorizer {

    private final CurrentAuthentication currentAuthentication;

    /**
     * Checks if the currently authenticated user is the owner of the resource, or has ADMIN authority.
     *
     * @param identity
     *         the identity to check against
     * @return true if the current user is the owner or an admin
     */
    public boolean isOwner(Long identity) {
        if (currentAuthentication.getIdentity() == null) {
            return false;
        }
        return currentAuthentication.hasAuthority(Authority.ADMIN) ||
                currentAuthentication.getIdentity().equals(identity);
    }
}
