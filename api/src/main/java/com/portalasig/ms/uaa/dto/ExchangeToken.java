package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO representing a JWT-based authentication response.
 * <p>
 * Contains access and refresh tokens, along with metadata such as issuance time,
 * expiration, and user/client identifiers.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Api(value = "ExchangeToken DTO")
public class ExchangeToken {

    @ApiModelProperty(value = "Access Token with JWT encoding")
    private String accessToken;

    @ApiModelProperty(value = "Refresh Token with JWT encoding")
    private String refreshToken;

    @ApiModelProperty(value = "When was this token issued")
    private Instant issuedAt;

    @ApiModelProperty(value = "Token duration in milliseconds")
    private long expiresIn;

    @ApiModelProperty(value = "Client identifier")
    private String clientId;

    @ApiModelProperty(value = "user identifier")
    private String username;
}
