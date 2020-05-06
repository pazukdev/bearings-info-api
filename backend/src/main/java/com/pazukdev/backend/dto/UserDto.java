package com.pazukdev.backend.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDto extends AbstractDto {

    private String email;
    private String password;
    private String repeatedPassword;
    private String img;
    private String country;
    @ApiModelProperty(hidden = true)
    private String role = "USER";
    private Long wishListId;
    private Integer rating = 0;
    private String activationUrl;

}
