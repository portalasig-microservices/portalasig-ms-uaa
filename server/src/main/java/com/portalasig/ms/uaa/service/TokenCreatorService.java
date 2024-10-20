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

@Service
@AllArgsConstructor
public final class TokenCreatorService {

    @Value("${ms.uaa.password-grant.default-client-id}")
    private final String usersClientId;

    private final JwtEncoder jwtEncoder;

    public Jwt createAccessToken(Authentication authentication, String username) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims = JwtClaimsSet.builder().subject(authentication.getName())
                .audience(List.of(usersClientId))
                .notBefore(now).issuer("http://localhost:5860/portalasig/uaa").issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claims(claims -> claims.putAll(JwtTokenHelper.createAccessTokenClaims(
                        authentication,
                        username)))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }

    public Jwt createRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims =
                JwtClaimsSet.builder().subject(authentication.getName()).issuedAt(now)
                        .expiresAt(now.plus(30, ChronoUnit.DAYS))
                        .claims(claims -> claims
                                .putAll(JwtTokenHelper.createAccessTokenClaims(
                                        authentication,
                                        authentication.getName()
                                )))
                        .build();
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }

    public Jwt refreshAccessToken(Jwt decodedJwt) {
        Instant now = Instant.now();
        JwtClaimsSet tokenClaims = JwtClaimsSet
                .builder()
                .subject(decodedJwt.getSubject())
                .audience(List.of(usersClientId))
                .notBefore(now).issuer("http://localhost:5860/portalasig/uaa").issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claims(claims -> claims.putAll(JwtTokenHelper.createAccessTokenClaims(decodedJwt))).build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, tokenClaims));
    }
}
