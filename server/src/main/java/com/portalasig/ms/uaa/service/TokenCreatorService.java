package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.utils.JwtTokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service responsible for creating and refreshing JWT access and refresh tokens used for authentication and
 * authorization flows, including password recovery.
 */
@Service
@AllArgsConstructor
public final class TokenCreatorService {

    /**
     * Constant representing the token type for password recovery flow.
     */
    public static final String PASSWORD_RECOVERY_TOKEN_TYPE = "password_recovery";

    @Value("${ms.uaa.password-grant.default-client-id}")
    private final String usersClientId;

    private final JwtEncoder jwtEncoder;

    /**
     * Generates an access token with 1-hour validity.
     *
     * @param authentication
     *         the authenticated principal
     * @param username
     *         the username to include in claims
     * @return a JWT access token
     */
    public Jwt createAccessToken(Authentication authentication, String username) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .audience(List.of(usersClientId))
                .notBefore(now)
                .issuer("http://localhost:5860/portalasig/uaa")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claims(claims -> claims.putAll(JwtTokenHelper.createAccessTokenClaims(authentication, username)))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }

    /**
     * Generates a refresh token with 30-day validity.
     *
     * @param authentication
     *         the authenticated principal
     * @param username
     *         the username to include in claims
     * @return a JWT refresh token
     */
    public Jwt createRefreshToken(Authentication authentication, String username) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .claims(claims -> claims.putAll(JwtTokenHelper.createAccessTokenClaims(authentication, username)))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }

    /**
     * Refreshes the access token using a decoded refresh token.
     *
     * @param decodedJwt
     *         the previously decoded refresh token
     * @return a new JWT access token
     */
    public Jwt refreshAccessToken(Jwt decodedJwt) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims = JwtClaimsSet.builder()
                .subject(decodedJwt.getSubject())
                .audience(List.of(usersClientId))
                .notBefore(now)
                .issuer("http://localhost:5860/portalasig/uaa")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claims(claims -> claims.putAll(JwtTokenHelper.createAccessTokenClaims(decodedJwt)))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }

    /**
     * Generates a short-lived token for password recovery with a 5-minute expiry.
     *
     * @param identity
     *         the identity of the user requesting recovery
     * @return the password recovery token as a string
     */
    public String createPasswordResetToken(Long identity) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(identity.toString())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .claim("type", PASSWORD_RECOVERY_TOKEN_TYPE)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
