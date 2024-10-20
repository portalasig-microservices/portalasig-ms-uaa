package com.portalasig.ms.uaa.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public final class JwtTokenHelper {

    private JwtTokenHelper() {
    }

    public static Map<String, ?> createAccessTokenClaims(Jwt decodedJwt) {
        return Map.of("authorities", decodedJwt.getClaim("authorities"), "user_identity",
                decodedJwt.getClaim("user_identity"));
    }

    public static Map<String, ?> createAccessTokenClaims(Authentication authentication, String username) {
        return Map.of(
                "authorities",
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                "username", username
        );
    }
}
