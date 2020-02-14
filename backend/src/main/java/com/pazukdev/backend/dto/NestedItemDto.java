package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.SpecificStringUtil;
import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

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
    private String creatorName;
    private Long creatorId;
    private Integer rating = 0;

    private String buttonText = "-";
    private String selectText = "-";

    private String comment = "-";
    private String secondComment = "-";

    private String vehicleIcon;
    private String vehicleClass;
    private String translatedVehicleClass;

    public void setVehicleClass(final String vehicleClass) {
        this.vehicleClass = vehicleClass;
        this.translatedVehicleClass = vehicleClass;
    }

    public void translate(final String langFrom, final String langTo, final List<String> dictionary) {
        if (SpecificStringUtil.isEmpty(langFrom) || SpecificStringUtil.isEmpty(langTo)) {
            return;
        }
        if (langTo.equals("en")) {
            translateToEnglish(langFrom, dictionary);
            return;
        }
        if (langFrom.equals("en")) {
            translateToLang(langTo, dictionary);
        }
    }

    private void translateToLang(final String langTo, final List<String> dictionary) {
        final String langFrom = "en";
        final boolean name = false;
        final boolean addToDictionary = false;

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, name, addToDictionary, dictionary);
        buttonText = TranslatorUtil.translate(langFrom, langTo, buttonText, name, addToDictionary, dictionary);
        selectText = TranslatorUtil.translate(langFrom, langTo, selectText, name, addToDictionary, dictionary);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, name, addToDictionary, dictionary);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, name, addToDictionary, dictionary);
        translatedVehicleClass = TranslatorUtil.translate(langFrom, langTo, vehicleClass, name, addToDictionary, dictionary);
    }

    private void translateToEnglish(final String langFrom, final List<String> dictionary) {
        final String langTo = "en";

        if (langFrom.equals(langTo)) {
            return;
        }

        final boolean name = false;
        final boolean addToDictionary = true;

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, name, addToDictionary, dictionary);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, name, addToDictionary, dictionary);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, name, addToDictionary, dictionary);
    }

}
