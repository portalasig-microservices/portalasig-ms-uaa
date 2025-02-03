package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "Admin Upsert User request")
public class UserEditPasswordRequest {

    @ApiModelProperty(value = "The user password")
    @NotNull
    private String password;

}
