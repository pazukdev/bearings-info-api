package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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

    public void translate(final String langTo, final List<String> dictionary) {
        boolean name = false;
        boolean addToDictionary = false;

        actionType = TranslatorUtil.translate("en", langTo, actionType, name, addToDictionary, dictionary);
        itemType = TranslatorUtil.translate("en", langTo, itemType, name, addToDictionary, dictionary);
        itemCategory = TranslatorUtil.translate("en", langTo, itemCategory, name, addToDictionary, dictionary);
        itemName = TranslatorUtil.translate("en", langTo, itemName, true, addToDictionary, dictionary);
        parentName = TranslatorUtil.translate("en", langTo, parentName, true, addToDictionary, dictionary);
    }

}
