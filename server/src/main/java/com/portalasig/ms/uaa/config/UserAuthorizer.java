package com.portalasig.ms.uaa.config;

import com.portalasig.ms.commons.constants.Authority;
import com.portalasig.ms.commons.rest.security.CurrentAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthorizer {

    private final CurrentAuthentication currentAuthentication;

    public boolean isOwner(Long identity) {
        if (currentAuthentication.getIdentity() == null) {
            return false;
        }
        return currentAuthentication.hasAuthority(Authority.ADMIN) ||
                currentAuthentication.getIdentity().equals(identity);
    }
}
