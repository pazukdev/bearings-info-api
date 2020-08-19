package com.pazukdev.backend.util;

import com.pazukdev.backend.constant.Constant;
import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.UserActionDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.dto.view.ItemView;

import java.util.*;

import static com.pazukdev.backend.dto.DictionaryData.getDictionaryFromFile;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.validator.CodeValidator.isLangCodeValid;

/**
 * @author Siarhei Sviarkaltsau
 */
public class TranslatorUtil {

    private static String WORD_SEPARATOR = " ";
    public static String DICTIONARY_SEPARATOR = "=";

    public static void translate(final String langFrom,
                                 final String langTo,
                                 final ItemView view,
                                 final boolean addToDictionary) throws Exception {
        if (!isLangCodeValid(langFrom) || !isLangCodeValid(langTo)) {
            return;
        }
        final String lang = !langFrom.equals("en") ? langFrom : langTo;
        final DictionaryData dictionaryData = getDictionaryFromFile(lang);
        translate(langFrom, langTo, view, addToDictionary, dictionaryData.getDictionary());
        view.setLang(langTo);
    }

    public static void translate(final String langFrom,
                                 final String langTo,
                                 final ItemView view,
                                 final boolean addToDictionary,
                                 final List<String> dictionary) throws Exception {

        HeaderTable header = view.getHeader();
        final String category = view.getCategory();
        final String localizedName = view.getLocalizedName();
        final ReplacersTable replacersTable = view.getReplacersTable();
        final List<String> categories = view.getAllCategories();

        try {

            header = translate(langFrom, langTo, header, addToDictionary, dictionary);
            translate(langFrom, langTo, replacersTable, dictionary);
            translate(langFrom, langTo, categories, addToDictionary, dictionary);

            boolean name = langTo.equals("en");

            view.setLocalizedCategory(translate(langFrom, langTo, category, false, addToDictionary, dictionary));
            view.setLocalizedName(translate(langFrom, langTo, localizedName, name, false, dictionary));
            view.setChildren(translateItemDtoList(langFrom, langTo, view.getChildren(), dictionary));
            view.setAllChildren(translateItemDtoList(langFrom, langTo, view.getAllChildren(), dictionary));
//            view.setPossibleParts(translateItemDtoList(langFrom, langTo, view.getPossibleParts(), dictionary));
//            view.setPossibleReplacers(translateItemDtoList(langFrom, langTo, view.getPossibleReplacers(), dictionary));

            if (!langTo.equals("en")) {
                for (final UserActionDto userAction : view.getLastVehicles()) {
                    userAction.translate(langTo, dictionary);
                }
                for (final UserActionDto userAction : view.getLastReplacers()) {
                    userAction.translate(langTo, dictionary);
                }
            }

            if (view.getParents() != null) {
                translate(langFrom, langTo, view.getParents(), addToDictionary, dictionary);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new Exception("Translation isn't finished because of the error. "
                    + "To remove this message please select English language");
        }

        view.setHeader(header);
        view.setAllCategories(categories);
    }

    private static HeaderTable translate(final String langFrom,
                                         final String langTo,
                                         final HeaderTable headerTable,
                                         boolean addToDictionary,
                                         final List<String> dictionary) {
        if (headerTable == null) {
            return null;
        }

        boolean name;

        for (final HeaderTableRow row : headerTable.getRows()) {
            if (!langTo.equals("en")) {
                name = false;
            } else {
                name = row.getName().equalsIgnoreCase(CategoryUtil.Parameter.DescriptionIgnored.NAME)
                        || row.getName().equalsIgnoreCase(CategoryUtil.Category.MANUFACTURER);
                if (name) {
                    addToDictionary = false;
                }
            }

            row.setParameter(translate(langFrom, langTo, row.getParameter(), false, true, dictionary));

            final String value = row.getValue();
            if (value.contains("; ")) {
                String translatedValue = "";
                for (final String word : value.split("; ")) {
                    final String translatedWord = translate(langFrom, langTo, word, name, addToDictionary, dictionary);
                    translatedValue += translatedWord + "; ";
                }
                row.setValue(removeLastChar(translatedValue.trim()));
            } else {
                row.setValue(translate(langFrom, langTo, value, name, addToDictionary, dictionary));
            }
        }

        return headerTable;
    }

    private static void translate(final String langFrom,
                                  final String langTo,
                                  final ReplacersTable replacersTable,
                                  final List<String> dictionary) {
        if (replacersTable == null) {
            return;
        }
        translateItemDtoList(langFrom, langTo, replacersTable.getReplacers(), dictionary);
    }

    public static List<NestedItemDto> translateItemDtoList(final String langFrom,
                                                           final String langTo,
                                                           final List<NestedItemDto> dtos,
                                                           final List<String> dictionary) {
        if (langFrom.equals("en")) {
            if (dtos.size() > Constant.COLLECTION_SIZE_TO_PARALLELIZE) {
                dtos.parallelStream().forEach(dto -> dto.translateToLang(langTo, dictionary));
            } else {
                dtos.forEach(dto -> dto.translateToLang(langTo, dictionary));
            }
        }
        return dtos;
    }

    private static void translate(final String languageFrom,
                                  final String languageTo,
                                  final List<String> list,
                                  final boolean addToDictionary,
                                  final List<String> dictionary) {
        final List<String> copy = new ArrayList<>(list);
        list.clear();
        for (final String s : copy) {
            list.add(translate(languageFrom, languageTo, s, false, addToDictionary, dictionary));
        }
        list.sort(String::compareTo);
    }

    public static String translate(final String langFrom,
                                   final String langTo,
                                   String text,
                                   boolean name,
                                   final boolean addToDictionary,
                                   final List<String> dictionary) {

        if (text == null) {
            return null;
        }

        text = text.trim();

        if (text.equals("-") || text.equals("no id") || text.equals("") || text.contains(".png")) {
            return text;
        }
        if (langFrom.equals(langTo)) {
            return text;
        }

        if (langFrom.equals("en")) {
//            name = false;

            String translated = getValueFromDictionary(text, langTo, dictionary);
            if (!isTranslated(translated, text)) {
                if (name) {
                    return text;
                }
                translated = parseAndTranslate(langTo, text, dictionary);
                if (!isTranslated(translated, text)) {
                    return text;
                }
            }
            return translated;
        } else {
            if (name) {
                return getValueFromDictionary(text, langTo, dictionary);
            }
//            return translateToEnglish(langFrom, text, addToDictionary, dictionary);
            return parseAndTranslate(langTo, text, dictionary);
        }
    }

    public static boolean isTranslated(final String translated, final String original) {
        return translated != null && !translated.equalsIgnoreCase(original);
    }

    private static String parseAndTranslate(final String langTo, String text, final List<String> dictionary) {
        final Map<String, String> map = new HashMap<>();
        int i = 0;
        final String s = "#";
        for (final List<String> subList : getAllSubListsSortedBySize(splitIntoWords(text))) {
            final String toTranslate = wordsIntoText(subList);
            final String translated = getValueFromDictionary(toTranslate, langTo, dictionary);
            if (translated != null && !translated.equalsIgnoreCase(toTranslate)) {
                final String key = s + i++;
                text = text.replace(toTranslate, key);
                map.put(key, translated);
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }

    private static List<List<String>> getAllSubListsSortedBySize(final List<String> words) {
        final List<String> subArraysAsStrings = new ArrayList<>();
        for ( int i = 0; i < words.size(); i++) {
            String s = "";
            for (int j = i; j < words.size(); j++) {
                s += words.get(j) + " ";
                subArraysAsStrings.add(s.trim());
            }
        }
        final List<List<String>> subArrays = new ArrayList<>();
        for (final String subArrayAsString : subArraysAsStrings) {
            subArrays.add(Arrays.asList(subArrayAsString.split(WORD_SEPARATOR)));
        }
        subArrays.sort(Comparator.comparing(List::size, Comparator.reverseOrder()));
        return subArrays;
    }

    public static boolean isInEnglish(final String text) {
        return !containsCyrillic(text);
    }

    private static boolean containsCyrillic(final String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.UnicodeBlock.of(text.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)) {
                return true;
            }
        }
        return false;
    }

    private static boolean translationResultIsBroken(final String translationResult) {
        return translationResult.contains("??") || translationResult.contains("? ?");
    }

    private static String getValueFromDictionary(String value,
                                                 final String lang,
                                                 final List<String> dictionary) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (isEmpty(value)) {
            return value;
        }

        if (containsOnlyDigitsAndDash(value)) {
            return value;
        }

        final boolean startsWithUppercase = startsWithUppercase(value);

        if (endChars.contains(getLastChar(value)) && value.length() > 1) {
            final String beforeLastChar = removeLastChar(value);
            final String translatedBeforeLastChar = getValueFromDictionary(beforeLastChar, lang, dictionary);
            return value.replaceFirst(beforeLastChar, translatedBeforeLastChar);
        }

        if (isSingleWord(value)) {
            if (isName(value)) {
                String translated = find(value, lang, dictionary);
                if (isFound(value, translated)) {
                    if (startsWithUppercase) {
                        translated = capitalize(translated);
                    }
                    return translated;
                }
                try {
                    final String beforeNumber = value.split(getSubstringWithFirstNumber(value))[0];
                    final String translatedBeforeNumber = getValueFromDictionary(beforeNumber, lang, dictionary);
                    return value.replaceFirst(beforeNumber, translatedBeforeNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isNumberWithUnit(value)) {
                final String number = getSubstringWithFirstNumber(value);
                if (number == null) {
                    return value;
                }
                final String afterNumber = value.replace(number, "");
                final String translatedAfterNumber = getValueFromDictionary(afterNumber, lang, dictionary);
                return value.replaceFirst(afterNumber, translatedAfterNumber);
            }

            if (isBetweenParenthesises(value)) {
                return "(" + getValueFromDictionary(getStringBetweenParentheses(value), lang, dictionary) + ")";
            }
        }

        String translated = find(value, lang, dictionary);
        if (startsWithUppercase) {
            translated = capitalize(translated);
        }
        return translated;
    }

    private static String find(final String value, final String lang, final List<String> dictionary) {
        String translated = value;
        for (final String line : dictionary) {
            if (line.split(DICTIONARY_SEPARATOR).length < 3) {
                continue;
            }
            if (lang.equals("en")) {
                if (line.split(DICTIONARY_SEPARATOR)[2].equalsIgnoreCase(value)) {
                    translated = line.split(DICTIONARY_SEPARATOR)[1];
                    break;
                }
            } else {
                if (line.toLowerCase()
                        .contains(lang + DICTIONARY_SEPARATOR + value.toLowerCase() + DICTIONARY_SEPARATOR)) {
                    translated = line.split(DICTIONARY_SEPARATOR)[2];
                    if (translationResultIsBroken(translated)) {
                        return value;
                    }
                    break;
                }
            }
        }
        return translated;
    }

    public static byte[] getSomeBytes0() {
        return Constant.EMAIL.getBytes();
    }

    private static boolean isFound(final String input, final String output) {
        return !input.equalsIgnoreCase(output);
    }

}
