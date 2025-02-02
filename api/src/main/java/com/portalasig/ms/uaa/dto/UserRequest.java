package com.portalasig.ms.uaa.dto;

import com.portalasig.ms.uaa.constant.UserRole;
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
public class UserRequest {

    @ApiModelProperty(value = "The user identity number")
    @NotNull
    private Long identity;

    @ApiModelProperty(value = "The first name of the user")
    @NotNull
    private String firstName;

    @ApiModelProperty(value = "The last name of the user")
    @NotNull
    private String lastName;

    @ApiModelProperty(value = "The email of the user")
    @NotNull
    private String email;

    @ApiModelProperty(value = "The user role")
    private UserRole userRole;
}
