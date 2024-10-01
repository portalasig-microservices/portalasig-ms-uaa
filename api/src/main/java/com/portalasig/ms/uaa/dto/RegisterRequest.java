package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the registration request")
public class RegisterRequest {

    @ApiModelProperty(notes = "The email of the user", required = true)
    @NotNull
    private String email;

    @ApiModelProperty(notes = "The unique identity of the user", required = true)
    @NotNull
    private Long identity;

    @ApiModelProperty(notes = "The password of the user", required = true)
    @NotNull
    private String password;

    @ApiModelProperty(notes = "The first name of the user")
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user")
    private String lastName;

    @ApiModelProperty(notes = "The username of the user")
    private String username;
}
