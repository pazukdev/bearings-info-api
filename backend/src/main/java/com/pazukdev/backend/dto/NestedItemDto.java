package com.pazukdev.backend.dto;

import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.TranslatorUtil;
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
//    private String localizedItemCategory = "-";
    private String creatorName;
    private Integer rating = 0;

    private String buttonText = "-";
//    private String localizedButtonText = "-";
    private String selectText = "-";
//    private String localizedSelectText = "-";

    private String comment = "-";
//    private String localizedComment = "-";
    private String secondComment = "-";
//    private String localizedSecondComment = "-";

    public void setItemCategory(final String itemCategory) {
        this.itemCategory = itemCategory;
//        this.localizedItemCategory = itemCategory;
    }

    public void setButtonText(final String buttonText) {
        this.buttonText = buttonText;
//        this.localizedButtonText = buttonText;
    }

    public void setSelectText(final String selectText) {
        this.selectText = selectText;
//        this.localizedSelectText = selectText;
    }

    public void setComment(final String comment) {
        this.comment = comment;
//        this.localizedComment = comment;
    }

    public void setSecondComment(final String secondComment) {
        this.secondComment = secondComment;
//        this.localizedSecondComment = secondComment;
    }

    public void translate(final String langFrom, final String langTo, final ItemService service) {
        if (langTo.equals("en")) {
            translateToEnglish(langFrom, service);
            return;
        }

        if (langFrom.equals("en")) {
            translateToLang(langTo, service);
        }
    }

    private void translateToLang(final String langTo, final ItemService service) {
        final String langFrom = "en";
        final boolean addToDictionary = false;
        final boolean parse = false;

//        localizedItemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, addToDictionary, parse, service);
//        localizedButtonText = TranslatorUtil.translate(langFrom, langTo, buttonText, addToDictionary, parse, service);
//        localizedSelectText = TranslatorUtil.translate(langFrom, langTo, selectText, addToDictionary, parse, service);
//        localizedComment = TranslatorUtil.translate(langFrom, langTo, comment, addToDictionary, parse, service);
//        localizedSecondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, addToDictionary, parse, service);

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, addToDictionary, parse, service);
        buttonText = TranslatorUtil.translate(langFrom, langTo, buttonText, addToDictionary, parse, service);
        selectText = TranslatorUtil.translate(langFrom, langTo, selectText, addToDictionary, parse, service);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, addToDictionary, parse, service);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, addToDictionary, parse, service);
    }

    private void translateToEnglish(final String langFrom, final ItemService service) {
        final String langTo = "en";
        final boolean addToDictionary = true;
        final boolean parse = false;

        if (langFrom.equals(langTo)) {
            return;
        }

//        itemCategory = TranslatorUtil.translate(langFrom, langTo, localizedItemCategory, addToDictionary, parse, service);
//        comment = TranslatorUtil.translate(langFrom, langTo, localizedComment, addToDictionary, parse, service);
//        secondComment = TranslatorUtil.translate(langFrom, langTo, localizedSecondComment, addToDictionary, parse, service);

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, addToDictionary, parse, service);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, addToDictionary, parse, service);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, addToDictionary, parse, service);
    }

}
