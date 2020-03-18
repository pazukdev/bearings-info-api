package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.FileUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class DictionaryData implements Serializable {

    private final static long serialVersionUID = 12343L;

    public static final String SEPARATOR = "=";

    private String lang;
    private List<String> langs;
    private List<String> dictionary;
    private String dictionaryId;

    public static DictionaryData getDictionaryFromFile(String lang) throws Exception {
        if (lang == null) {
            lang = "en";
        } else {
            lang = SpecificStringUtil.removeUtf8BOM(lang.toLowerCase());
        }

        TranslatorUtil.validate(lang);

        final Set<String> langs = new HashSet<>();
        langs.add("en");
        String filename = null;
        final List<String> langsData = FileUtil.readGoogleDocDocument(FileUtil.FileName.LANGS);
        for (final String langData : langsData) {
            if (SpecificStringUtil.isEmpty(langData) || !langData.contains(SEPARATOR)) {
                continue;
            }
            final String[] data = langData.replaceAll(" ", "").split(SEPARATOR);
            final String langCode = SpecificStringUtil.removeUtf8BOM(data[0].toLowerCase());
            if (langCode.length() != 2) {
                continue;
            }
            langs.add(langCode);
            if (!lang.equals("en") && lang.equalsIgnoreCase(langCode)) {
                filename = data[1];
            }
        }

        final DictionaryData dictionaryData = new DictionaryData();
        dictionaryData.setDictionary(lang.equals("en") ? new ArrayList<>() : FileUtil.readGoogleDocDocument(filename));
        dictionaryData.setLangs(new ArrayList<>(langs));
        dictionaryData.setLang(lang);
        dictionaryData.setDictionaryId(filename);
        return dictionaryData;
    }

}
