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
@ApiModel(description = "Email address request")
public class EmailAddressRequest {

    @ApiModelProperty(value = "email address")
    @NotNull
    private String email;
}
