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
@ApiModel(description = "Details about the registration request")
public class RegisterRequest {

    @ApiModelProperty(value = "The email of the user", required = true)
    @NotNull
    private String email;

    @ApiModelProperty(value = "The unique identity of the user", required = true)
    @NotNull
    private Long identity;

    @ApiModelProperty(value = "The password of the user", required = true)
    @NotNull
    private String password;

    @ApiModelProperty(value = "The first name of the user")
    private String firstName;

    @ApiModelProperty(value = "The last name of the user")
    private String lastName;

    @ApiModelProperty(value = "The username of the user")
    private String username;

}
