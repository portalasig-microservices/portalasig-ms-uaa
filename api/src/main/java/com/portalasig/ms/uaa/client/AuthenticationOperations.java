package com.portalasig.ms.uaa.client;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.ExchangeToken;
import com.portalasig.ms.uaa.dto.LoginRequest;
import com.portalasig.ms.uaa.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(RestConstants.VERSION_ONE + RestPaths.Authentication.AUTHENTICATION)
public interface AuthenticationOperations {

    @PostExchange(RestPaths.Authentication.LOGIN)
    ExchangeToken loginAndGenerateTokens(@RequestBody @Valid LoginRequest loginRequest);

    @PostExchange(RestPaths.Authentication.REFRESH_TOKEN)
    ExchangeToken refreshAccessToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest);
}
