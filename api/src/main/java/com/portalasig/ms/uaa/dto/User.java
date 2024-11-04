package com.portalasig.ms.uaa.dto;

import com.portalasig.ms.uaa.constant.EmailSetting;
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

    @ApiModelProperty(value = "The unique identifier of the user")
    private Long userId;

    @ApiModelProperty(value = "The username of the user")
    private String username;

    @ApiModelProperty(value = "The email of the user")
    private String email;

    @ApiModelProperty(value = "The first name of the user")
    private String firstName;

    @ApiModelProperty(value = "The last name of the user")
    private String lastName;

    @ApiModelProperty(value = "The unique identity of the user")
    private Long identity;

    @ApiModelProperty(value = "The roles assigned to the user")
    private List<UserRole> roles;

    @ApiModelProperty(value = "Which emails users desired to receive", example = "EVENT, EVALUATION, ASSIGNMENT")
    private List<EmailSetting> emailSettings;
}
