package com.portalasig.ms.uaa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portalasig.ms.uaa.constant.EmailSetting;
import com.portalasig.ms.uaa.constant.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO that represents a user within the PortalAsig system.
 * <p>
 * Includes identifying attributes such as ID, username, and email, as well as user metadata like roles and configured
 * email settings.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

    /**
     * Returns the full name of the user by concatenating the first and last name.
     *
     * @return the user's full name in the format "John Doe"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
