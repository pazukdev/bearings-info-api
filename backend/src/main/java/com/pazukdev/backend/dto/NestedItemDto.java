package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String manufacturer = "-";
    private String size;
    private String creatorName;
    private Long creatorId;
    private Integer rating = 0;
    private String type;

    private Set<NestedItemDto> likedUsers = new HashSet<>();
    private Set<NestedItemDto> dislikedUsers = new HashSet<>();

    private String buttonText = "-";

    private String comment = "-";
    private String secondComment = "-";

    private String vehicleIcon;
    private String vehicleClass;

    public void translateToLang(final String langTo, final List<String> dictionary) {
        final String langFrom = "en";
        final boolean name = false;
        final boolean addToDictionary = false;

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, name, addToDictionary, dictionary);
        buttonText = TranslatorUtil.translate(langFrom, langTo, buttonText, name, addToDictionary, dictionary);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, name, addToDictionary, dictionary);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, name, addToDictionary, dictionary);
        manufacturer = TranslatorUtil.translate(langFrom, langTo, manufacturer, name, addToDictionary, dictionary);
    }

}
