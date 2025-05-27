package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used for restoring a user's password using a recovery token.
 * <p>
 * Contains the recovery token and the new password to apply.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "User restore password request")
public class UserRestorePasswordRequest {

    @ApiModelProperty(value = "The user identity number")
    @NotNull
    private String recoveryToken;

    @ApiModelProperty(value = "The new password of the user")
    @NotNull
    private String password;
}
