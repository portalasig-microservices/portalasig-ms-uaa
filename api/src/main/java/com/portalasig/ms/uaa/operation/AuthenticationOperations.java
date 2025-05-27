package com.portalasig.ms.uaa.operation;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Defines authentication-related operations for the PortalAsig ecosystem.
 * Supports login and token refresh using OAuth2 standards.
 */
@HttpExchange(RestConstants.VERSION_ONE + RestPaths.Authentication.AUTHENTICATION)
public interface AuthenticationOperations {

    /**
     * Authenticates a user using their credentials and generates an access and refresh token pair.
     *
     * @param loginRequest the credentials (username and password)
     * @return a new {@link ExchangeToken} containing access and refresh tokens
     */
    @PostExchange(RestPaths.Authentication.LOGIN)
    ExchangeToken loginAndGenerateTokens(@RequestBody @Valid LoginRequest loginRequest);

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshTokenRequest the refresh token payload
     * @return a new {@link ExchangeToken} with a refreshed access token
     */
    @PostExchange(RestPaths.Authentication.REFRESH_TOKEN)
    ExchangeToken refreshAccessToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest);
}
