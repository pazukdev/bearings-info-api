package com.pazukdev.backend.dto;

import com.pazukdev.backend.util.TranslatorUtil;
import lombok.Data;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.pazukdev.backend.util.FileUtil.getSortedFileLines;
import static com.pazukdev.backend.util.FileUtil.getTxtFileTextLines;
import static com.pazukdev.backend.util.TranslatorUtil.getDictionaryFilePath;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class DictionaryData implements Serializable {

    private final static long serialVersionUID = 12343L;

    private String lang;
    private List<String> langs;
    private List<String> dictionary;

    public static DictionaryData getDictionaryFromFile(final String lang) throws Exception {
        TranslatorUtil.validate(lang);
        final DictionaryData dictionaryData = new DictionaryData();
        if (lang.equals("en")) {
            dictionaryData.setDictionary(new ArrayList<>());
        } else {
            dictionaryData.setDictionary(getTxtFileTextLines(getDictionaryFilePath(lang)));
        }
        dictionaryData.setLangs(getTxtFileTextLines("langs"));
        dictionaryData.setLang(lang);
        return dictionaryData;
    }

    public static DictionaryData createDictionary(final String text) throws Exception {
        return createDictionary(Arrays.asList(text.split(System.getProperty("line.separator"))));
    }

    public static DictionaryData createDictionary(final List<String> dictionary) throws Exception {
        dictionary.sort(String::compareTo);
        final String lang;
        final String firstLineLang = dictionary.get(0).split(TranslatorUtil.DICTIONARY_SEPARATOR)[0];
        final String lastLineLang = dictionary.get(dictionary.size() - 1).split(TranslatorUtil.DICTIONARY_SEPARATOR)[0];
        if (firstLineLang != null && firstLineLang.equalsIgnoreCase(lastLineLang)) {
            lang = firstLineLang;
        } else {
            throw new Exception("Dictionary is not accepted: dictionary contains more then 1 languages");
        }

        final DictionaryData dictionaryData = new DictionaryData();
        dictionaryData.setDictionary(dictionary);
        dictionaryData.setLang(lang);

        return dictionaryData;
    }

    public static void saveDictionary(final DictionaryData dictionaryData) throws Exception {
        validate(dictionaryData);
        final String lang = dictionaryData.getLang();
        final List<String> dictionary = dictionaryData.getDictionary();
        Files.write(getDictionaryFilePath(lang), getSortedFileLines(dictionary), StandardCharsets.UTF_8);
    }

    public static void validate(final DictionaryData dictionaryData) throws Exception {
        TranslatorUtil.validate(dictionaryData.getLang());
        if (dictionaryData.getDictionary().size() < 1) {
            throw new Exception("Empty dictionary");
        }
    }


}
