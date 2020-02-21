package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserActionDto extends AbstractDto {

    private Long userId;
    private Long itemId;
    private Long parentId;
    private String userName;
    private String itemName;
    private String parentName;
    private String actionType;
    private String itemType;
    private String itemCategory;
    private String date;

}
