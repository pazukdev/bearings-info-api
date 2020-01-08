package com.pazukdev.backend.dto.user;

import com.pazukdev.backend.dto.AbstractDto;
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
    private Integer rating = 0;
    @ApiModelProperty(hidden = true)
    private String role = "USER";
    private Long wishListId;

}
