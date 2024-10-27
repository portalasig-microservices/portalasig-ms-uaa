package com.portalasig.ms.uaa.dto;

import com.portalasig.ms.uaa.constant.EmailSetting;
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
@ApiModel(description = "Details about the user request")
public class UserRequest {

    @ApiModelProperty(value = "The first name of the user")
    private String firstName;

    @ApiModelProperty(value = "The last name of the user")
    private String lastName;

    @ApiModelProperty(value = "The email settings of the user")
    private List<EmailSetting> emailSettings;
}
