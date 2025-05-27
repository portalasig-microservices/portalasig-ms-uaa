package com.portalasig.ms.uaa.service;

import com.portalasig.ms.commons.rest.exception.SystemErrorException;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.temporal.ChronoUnit;

import static com.portalasig.ms.uaa.service.TokenCreatorService.PASSWORD_RECOVERY_TOKEN_TYPE;

/**
 * Service responsible for managing user authentication and token generation. Handles login, access token creation,
 * refresh token validation, and recovery token verification.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final TokenCreatorService tokenCreatorService;
    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;

    /**
     * Authenticates a user based on credentials and generates a new access and refresh token.
     *
     * @param request
     *         the login request containing username and password
     * @return a newly generated {@link ExchangeToken}
     * @throws SystemErrorException
     *         if authentication fails
     */
    public ExchangeToken loginAndGenerateTokens(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Jwt accessToken = tokenCreatorService.createAccessToken(authentication, request.getUsername());
            Jwt refreshToken = tokenCreatorService.createRefreshToken(authentication, request.getUsername());

            return ExchangeToken.builder()
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(refreshToken.getTokenValue())
                    .issuedAt(Instant.now())
                    .clientId(accessToken.getClaim("client_id"))
                    .expiresIn(ChronoUnit.HOURS.getDuration().toSeconds())
                    .username(request.getUsername())
                    .build();
        } catch (AuthenticationException e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
        }
    }

    /**
     * Validates a refresh token and issues a new access token.
     *
     * @param request
     *         the refresh token request
     * @return a new {@link ExchangeToken}
     * @throws SystemErrorException
     *         if the refresh token is invalid or expired
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

            return ExchangeToken.builder()
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(request.getRefreshToken())
                    .issuedAt(Instant.now())
                    .clientId(accessToken.getClaim("client_id"))
                    .expiresIn(ChronoUnit.HOURS.getDuration().toSeconds())
                    .username(decodedJwt.getClaim("username"))
                    .build();
        } catch (JwtException e) {
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token");
        }
    }

    /**
     * Validates if the provided recovery token is a valid password recovery token and not expired.
     *
     * @param token
     *         the JWT recovery token
     * @return true if the token is valid, false otherwise
     * @throws SystemErrorException
     *         if the token is invalid, expired, or of the wrong type
     */
    public boolean isPasswordRecoveryTokenValid(String token) {
        try {
            Jwt recoveryToken = jwtDecoder.decode(token);
            if (!PASSWORD_RECOVERY_TOKEN_TYPE.equals(recoveryToken.getClaimAsString("type"))) {
                log.error("Token has invalid type token_type={}", recoveryToken.getClaimAsString("type"));
                throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
            }
            Instant now = Instant.now();
            return recoveryToken.getExpiresAt() != null && recoveryToken.getExpiresAt().isAfter(now);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token has invalid expiration date");
            throw new SystemErrorException(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
        }
    }
}
