package com.portalasig.ms.uaa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the user request")
public class UserRequest {

    @ApiModelProperty(notes = "The first name of the user")
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user")
    private String lastName;
}
