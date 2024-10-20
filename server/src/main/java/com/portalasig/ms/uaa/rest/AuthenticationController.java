package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import com.portalasig.ms.uaa.service.AuthenticationService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.VERSION_ONE + RestPaths.Authentication.AUTHENTICATION)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @ApiModelProperty(value = "Login via API and generate access and refresh tokens")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 401, message = "Invalid username or password")})
    @PostMapping(RestPaths.Authentication.LOGIN)
    public ExchangeToken loginAndGenerateTokens(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.loginAndGenerateTokens(loginRequest);
    }

    @ApiModelProperty(value = "Refresh access token using refresh token")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Token refreshed successfully"),
            @ApiResponse(code = 401, message = "Invalid refresh token")})
    @PostMapping(RestPaths.Authentication.REFRESH_TOKEN)
    public ExchangeToken refreshAccessToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refreshAccessToken(refreshTokenRequest);
    }
}
