package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    @ApiModelProperty(value = "Client identifier")
    private String clientId;

    @ApiModelProperty(value = "user identifier")
    private String username;
}
