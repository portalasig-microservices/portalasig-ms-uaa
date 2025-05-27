package com.portalasig.ms.uaa.dto;

import com.portalasig.ms.uaa.constant.EmailSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * DTO used for updating the list of email settings associated with a user.
 * <p>
 * Each setting represents a type of notification the user can opt into or out of.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Email Setting request")
public class EmailSettingRequest {

    @ApiModelProperty(value = "List of email settings")
    private List<EmailSetting> emailSettings;
}
