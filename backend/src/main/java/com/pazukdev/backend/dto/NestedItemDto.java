package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NestedItemDto extends AbstractDto {

    private Long itemId;
    private String itemName = "-";
    private String itemCategory = "-";
    private String buttonText = "-";
    private String selectText = "-";
    private String comment = "-";
    private String localizedComment = "-";
    private String location = "-";
    private String quantity = "0";
    private String creatorName;
    private Integer rating = 0;

    public void setComment(String comment) {
        this.comment = comment;
        this.localizedComment = comment;
    }
}
