package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used by administrators to update a user's password manually.
 * <p>
 * Typically used in user management panels or recovery flows where direct password reset is needed.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Admin Upsert User request")
public class UserEditPasswordRequest {

    @ApiModelProperty(value = "The user password")
    @NotNull
    private String password;

}
