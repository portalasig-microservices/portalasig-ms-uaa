package com.portalasig.ms.uaa.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

/**
 * Utility class for extracting and creating JWT claims for access tokens.
 */
public final class JwtTokenHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private JwtTokenHelper() {
    }

    /**
     * Extracts access token claims from a decoded JWT.
     *
     * @param decodedJwt
     *         the decoded JWT token
     * @return a map containing the authorities and username extracted from the token
     */
    public static Map<String, ?> createAccessTokenClaims(Jwt decodedJwt) {
        return Map.of(
                "authorities", decodedJwt.getClaim("authorities"),
                "username", decodedJwt.getClaim("username")
        );
    }

    /**
     * Creates access token claims from the given authentication and username.
     *
     * @param authentication
     *         the Spring Security authentication object
     * @param username
     *         the username to embed in the token
     * @return a map containing the user's authorities and username
     */
    public static Map<String, ?> createAccessTokenClaims(Authentication authentication, String username) {
        return Map.of(
                "authorities",
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                "username", username
        );
    }
}
