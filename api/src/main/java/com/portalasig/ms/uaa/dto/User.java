package com.portalasig.ms.uaa.dto;

import com.portalasig.ms.uaa.constant.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the user")
public class User {

    @ApiModelProperty(notes = "The username of the user")
    private String username;

    @ApiModelProperty(notes = "The email of the user")
    private String email;

    @ApiModelProperty(notes = "The first name of the user")
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user")
    private String lastName;

    @ApiModelProperty(notes = "The unique identity of the user")
    private Long identity;

    @ApiModelProperty(notes = "The roles assigned to the user")
    private List<UserRole> roles;
}
