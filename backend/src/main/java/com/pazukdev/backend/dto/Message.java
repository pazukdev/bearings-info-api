package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class Message implements Serializable {

    private final static long serialVersionUID = 12343L;

    private String text;
    private String localizedText;

    public void translate(final String langTo, final List<String> dictionary) {
        final boolean name = false;
        final boolean addToDictionary = false;
        localizedText = TranslatorUtil.translate("en", langTo, text, name, addToDictionary, dictionary);
    }

}
