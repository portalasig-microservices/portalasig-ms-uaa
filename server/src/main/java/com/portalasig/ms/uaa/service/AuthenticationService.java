package com.portalasig.ms.uaa.service;

import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for managing user authentication and token generation. It handles login, access token creation,
 * and refresh token handling. You could say it's a custom implementation of OAuth grant_type password and
 * refresh_token.
 */
@Service
@AllArgsConstructor
public class AuthenticationService {

    private final TokenCreatorService tokenCreatorService;
    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;

    /**
     * Authenticates a user based on the provided login request and generates a new access token and refresh token.
     *
     * @param request
     *         the login request containing username and password
     * @return an {@link ExchangeToken} containing the access token, refresh token, clientId, and username
     * @throws SystemErrorException
     *         if the authentication fails due to invalid credentials
     */
    public ExchangeToken loginAndGenerateTokens(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Jwt accessToken = tokenCreatorService.createAccessToken(authentication, request.getUsername());
            Jwt refreshToken = tokenCreatorService.createRefreshToken(authentication);

            return ExchangeToken
                    .builder()
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(refreshToken.getTokenValue())
                    .issuedAt(Instant.now()).clientId(accessToken.getClaim("client_id"))
                    .username(request.getUsername())
                    .build();
        } catch (AuthenticationException e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
        }
    }

    /**
     * Refreshes the access token based on the provided refresh token request. If the refresh token is valid and has not
     * expired, a new access token is generated.
     *
     * @param request
     *         the refresh token request containing the refresh token
     * @return an {@link ExchangeToken} containing the new access token, the original refresh token, clientId, and
     * username @throws SystemErrorException if the refresh token is invalid or expired
     */
    public ExchangeToken refreshAccessToken(RefreshTokenRequest request) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(request.getRefreshToken());
            Instant now = Instant.now();
            Instant expiry = decodedJwt.getExpiresAt();

            if (expiry == null || expiry.isBefore(now)) {
                throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Refresh token has expired");
            }

            Jwt accessToken = tokenCreatorService.refreshAccessToken(decodedJwt);

            return ExchangeToken
                    .builder()
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(request.getRefreshToken())
                    .issuedAt(Instant.now()).clientId(accessToken.getClaim("client_id"))
                    .username(decodedJwt.getClaim("username")).build();
        } catch (JwtException e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token");
        }
    }
}
