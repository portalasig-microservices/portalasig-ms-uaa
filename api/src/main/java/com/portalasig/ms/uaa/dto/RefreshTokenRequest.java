package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Api(value = "Refresh Token Request DTO")
public class RefreshTokenRequest {

    @ApiModelProperty(value = "Refresh token", required = true)
    @NotNull
    private String refreshToken;
}
