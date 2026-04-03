package com.portalasig.ms.uaa.dto;

import com.opencsv.bean.CsvBindByPosition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user record parsed from a CSV file.
 * <p>
 * This class is used for bulk user import operations and binds each field
 * to a specific position in the CSV row using {@link com.opencsv.bean.CsvBindByPosition}.
 * </p>
 * <p>
 * It includes user identification fields, metadata such as creation/update dates,
 * role information, and email settings.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the CSV user")
public class CsvUser {

    @ApiModelProperty(value = "The unique identity of the user")
    @CsvBindByPosition(position = 1)
    private Long identity;

    @ApiModelProperty(value = "The first name of the user")
    @CsvBindByPosition(position = 3)
    private String firstName;

    @ApiModelProperty(value = "The last name of the user")
    @CsvBindByPosition(position = 4)
    private String lastName;

    @ApiModelProperty(value = "The email of the user")
    @CsvBindByPosition(position = 5)
    private String email;

    @ApiModelProperty(value = "Indicates whether the user is active")
    @CsvBindByPosition(position = 6)
    private boolean active;

    @ApiModelProperty(value = "The date when the user was created")
    @CsvBindByPosition(position = 7)
    private String createdDate;

    @ApiModelProperty(value = "The date when the user was last updated")
    @CsvBindByPosition(position = 8)
    private String updatedDate;

    @ApiModelProperty(value = "The role of the user")
    @CsvBindByPosition(position = 9)
    private String role;

    @ApiModelProperty(value = "Email settings of the user as an integer-expressed bitmask")
    @CsvBindByPosition(position = 10)
    private Integer emailSettings;
}
