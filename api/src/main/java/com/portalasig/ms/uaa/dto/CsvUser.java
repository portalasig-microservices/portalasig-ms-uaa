package com.portalasig.ms.uaa.dto;

import com.opencsv.bean.CsvBindByPosition;
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
@ApiModel(description = "Details about the CSV user")
public class CsvUser {

    @ApiModelProperty(notes = "The email of the user")
    @CsvBindByPosition(position = 5)
    private String email;

    @ApiModelProperty(notes = "The first name of the user")
    @CsvBindByPosition(position = 3)
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user")
    @CsvBindByPosition(position = 4)
    private String lastName;

    @ApiModelProperty(notes = "The unique identity of the user")
    @CsvBindByPosition(position = 1)
    private Long identity;

    @ApiModelProperty(notes = "The role of the user")
    @CsvBindByPosition(position = 10)
    private String role;

    @ApiModelProperty(notes = "The date when the user was created")
    @CsvBindByPosition(position = 8)
    private String createdDate;

    @ApiModelProperty(notes = "The date when the user was last updated")
    @CsvBindByPosition(position = 9)
    private String updatedDate;

    @ApiModelProperty(notes = "Indicates whether the user is active")
    @CsvBindByPosition(position = 7)
    private boolean active;
}
