package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import com.portalasig.ms.uaa.operation.AuthenticationOperations;
import com.portalasig.ms.uaa.service.AuthenticationService;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for handling authentication operations such as login and token refresh.
 * <p>
 * Implements {@link AuthenticationOperations} to define the endpoints.
 */
@RestController
@RequiredArgsConstructor
@Api(value = "Authentication Controller", tags = "Authentication Management")
public class AuthenticationController implements AuthenticationOperations {

    private final AuthenticationService authenticationService;

    @Override
    public ExchangeToken loginAndGenerateTokens(@Valid LoginRequest loginRequest) {
        return authenticationService.loginAndGenerateTokens(loginRequest);
    }

    @Override
    public ExchangeToken refreshAccessToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refreshAccessToken(refreshTokenRequest);
    }
}
