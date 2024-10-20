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
@Api(value = "Login Request DTO")
public class LoginRequest {

    @ApiModelProperty(value = "The identity number of the user", required = true)
    @NotNull
    private String username;

    @ApiModelProperty(value = "The password of the user", required = true)
    @NotNull
    private String password;
}
