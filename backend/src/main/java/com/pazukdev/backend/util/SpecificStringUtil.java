package com.pazukdev.backend.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
public class SpecificStringUtil {

    public static final String WORD_SEPARATOR = " ";

    @Getter
    enum Position {

        BEFORE(0), AFTER(1), BETWEEN(-1), NOT_SPECIFIED(-2);

        private final int index;

        Position(final int index) {
            this.index = index;
        }

    }

    @Getter
    enum Separator {

        DASH("-"), OPEN_PAREN("\\("), CLOSE_PAREN(")"), SEMICOLON(";"), NOT_SPECIFIED("");

        private final String separator;

        Separator(final String separator) {
            this.separator = separator;
        }

    }

    public static final List<String> nullKeys = Arrays.asList("?", "-", "null");
    public static final List<String> abbreviation = Arrays
            .asList("imz", "kmz", "gost", "bmw", "ussr", "al", "usa", "fag");
    public final static List<Character> endChars = Arrays.asList('.', ',', ';', ':', '-', '?', '!');
    public final static List<String> units = Arrays
            .asList("mm", "cm", "m", "g", "kg", "km/h", "kmh", "mph", "s", "min", "n", "nm", "ml", "L", "t", "hp",
                    "rpm", "L/100km", "CC", "cc");

    public static List<String> getList(String source) {
        return Arrays.asList(removeSpaces(source).split(Separator.SEMICOLON.getSeparator()));
    }

    public static String removeSpaces(final String source) {
        if (isEmpty(source)) {
            return source;
        }
        return source.replaceAll(" ", "");
    }

    public static Integer getIntegerBetweenParentheses(@Nullable final String source) {
        return getInteger(source, Position.BETWEEN, null);

    }

    public static String getStringBetweenParentheses(@Nullable final String source) {
        return getString(source, Position.BETWEEN, null);
    }

    public static String getStringBeforeParentheses(@Nullable final String source) {
        return getString(source, Position.BEFORE, Separator.OPEN_PAREN);
    }

    public static Integer getIntegerBeforeDash(final String source) {
        return getInteger(source, Position.BEFORE, Separator.DASH);
    }

    public static Integer getIntegerAfterDash(final String source) {
        return getInteger(source, Position.AFTER, Separator.DASH);
    }

    public static String getStringBeforeDash(final String source) {
        return getString(source, Position.BEFORE, Separator.DASH);
    }

    public static String getStringAfterDash(final String source) {
        return getString(source, Position.AFTER, Separator.DASH);
    }

    public static Integer getInteger(final String data) {
        if (isEmpty(data) || !StringUtils.isNumeric(data)) {
            return null;
        }
        return  Integer.valueOf(data);
    }

    public static String getString(final String source) {
        return getString(source, Position.NOT_SPECIFIED, Separator.NOT_SPECIFIED);
    }

    public static List<String> enumClassToCapitalizedStrings(final Class<? extends Enum<?>> enumClass) {
        return enumNamesToListOfStrings(getNames(enumClass));
    }

    public static String[] getNames(final Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::toString).toArray(String[]::new);
    }

    public static List<String> enumNamesToListOfStrings(final String[] names) {
        final List<String> refactoredNames = new ArrayList<>();
        for (final String name : names) {
            refactoredNames.add(enumNameToCapitalizedLowerCaseString(name));
        }
        return refactoredNames;
    }

    public static String capitalizeAfterDot(final String s) {
        if (s == null) {
            return null;
        }
        if (isEmpty(s)) {
            return s;
        }
        String capitalized = "";
        for (final String sentence : s.split("\\. ")) {
            capitalized += capitalize(sentence) + ". ";
        }
        return removeLastChar(capitalized.trim());
    }

    public static String capitalize(final String s) {
        if (isAbbreviation(s)) {
            return s.toUpperCase();
        }
        return StringUtils.capitalize(s);
    }

    public static String uncapitalize(final String s) {
        if (isAbbreviation(s)) {
            return s.toLowerCase();
        }
        return StringUtils.uncapitalize(s);
    }

    public static String enumNameToCapitalizedLowerCaseString(final String name) {
        return capitalize(name.replaceAll("_", " ").toLowerCase());
    }

    public static String enumToCapitalizedLowerCaseString(final Enum e) {
        return enumNameToCapitalizedLowerCaseString(e.name());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> T stringToEnum(final Class<T> enumClass, final String s) {
        return (T) Enum.valueOf(enumClass, stringToEnumName(s));
    }

    public static String stringToEnumName(final String s) {
        return s.replaceAll(" ", "_").toUpperCase();
    }

    public static boolean isNotLowerCasedEnumName(final String s) {
        return s.contains(" ") || !s.equals(s.toLowerCase());
    }

    public static List<String> toList(final String s) {
        return new ArrayList<>(Arrays.asList(s.split("")));
    }

    private static Integer getInteger(final String source, final Position position, final Separator separator) {
        return getInteger(getString(source, position, separator));
    }

    private static String getString(@NotNull final String source,
                                    @NotNull final Position position,
                                    @Nullable final Separator separator) {
        if (isEmpty(source)) {
            return null;
        }

        String result;

        if (position == Position.NOT_SPECIFIED) {
            result = source;
        } else if (position == Position.BETWEEN) {
            result = StringUtils.substringBetween(source, "(", ")");
        } else {
            if (separator == null || separator == Separator.NOT_SPECIFIED) {
                throw new IllegalArgumentException("Separator is not specified");
            }
            result = source.split(separator.getSeparator())[position.getIndex()];
        }

        result = StringUtils.trim(result);

        if (isEmpty(result)) {
            return null;
        }

        return result;
    }

    public static boolean hasNoData(@Nullable final String source) {
        return !hasData(source);
    }

    public static boolean hasData(@Nullable final String source) {
        return isNotEmpty(source) && source.contains("|");
    }

    public static boolean isNotEmpty(@Nullable final String data) {
        return !isEmpty(data);
    }

    public static boolean isEmpty(@Nullable final String data) {
        return StringUtils.isBlank(data) || isNullKey(data);
    }

    private static boolean isNullKey(final String source) {
        return nullKeys.contains(source);
    }

    private static boolean isAbbreviation(final String s) {
        return s != null && abbreviation.contains(s.toLowerCase());
    }

    public static Integer extractIntegerAutomatically(final String source) {
        if (containsParentheses(source)) {
            return getIntegerBetweenParentheses(source);
        }
        return getInteger(source);
    }

    public static Boolean containsParentheses(final String source) {
        if(isEmpty(source)) {
            return false;
        }
        return source.contains("(") && source.contains(")");
    }

    public static String replaceEmptyWithDash(final String s) {
        return isEmpty(s) ? "-" : s;
    }

    public static String fixParam(final String s) {
        if (isEmpty(s)) {
            return s;
        }
        return s.replaceAll(":", "");
    }

    public static String fixValue(final String s) {
        if (isEmpty(s)) {
            return s;
        }
        return fixSemicolons(s.replaceAll(":", ""));
    }

    public static String fixSemicolons(final String s) {
        return isEmpty(s) ? s : s.replaceAll("(;)+","; ").replaceAll("( )+"," ");
    }

    public static String replaceEmptyWithEmpty(final String s) {
        return isEmpty(s) ? "" : s;
    }

    public static List<String> splitIntoWords(final String text) {
        return new ArrayList<>(Arrays.asList(text.split(WORD_SEPARATOR)));
    }

    public static String wordsIntoText(final List<String> words) {
        String text = "";
        for (final String word : words) {
            text += word + WORD_SEPARATOR;
        }
        text = text.trim(); // to remove space at the end
        return text;
    }

    public static boolean startsWithUppercase(final String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        final String firstLetter = String.valueOf(s.charAt(0));
        return firstLetter.equals(firstLetter.toUpperCase());
    }

    public static Character getLastChar(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s.charAt(s.length() - 1);
    }

    public static String removeLastChar(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s.substring(0, s.length() - 1);
    }

    public static boolean isNumber(final String s) {
        return NumberUtils.isCreatable(s);
    }

    public static boolean isNumberWithUnit(String s) {
        if (!startsWithNumber(s)) {
            return false;
        }
        s = removeSpaces(s);
        String number = "";
        for (final Character c : s.toCharArray()) {
            if (isNumber(c.toString()) || c == '.') {
                number += c;
            }
        }
        final String unit = s.replace(number, "");
        return units.contains(unit.toLowerCase()) && s.equals(number + unit);
    }

    public static boolean isParameterWithUnit(String s) {
        for (final String unit : units) {
            if (stringEndsWithSubstring(s, unit)) {
                return true;
            }
        }
        return false;
    }

    public static String getUnitFromParameter(String s) {
        if (!isParameterWithUnit(s)) {
            return null;
        }
        for (final String unit : units) {
            if (stringEndsWithSubstring(s, unit)) {
                return unit;
            }
        }
        return null;
    }

    public static String getSubstringWithFirstNumber(final String s) {
        if (!containsNumber(s)) {
            return null;
        }
        boolean firstDigitFound = false;
        String number = "";
        for (final Character c : s.toCharArray()) {
            if (!firstDigitFound) {
                if (isNumber(c.toString())) {
                    firstDigitFound = true;
                    number += c;
                }
            } else {
                if (isNumber(c.toString()) || c == '.') {
                    number += c;
                } else {
                    break;
                }
            }
        }
        return number;
    }

    public static Double getFirstNumber(final String s) {
        if (!containsNumber(s)) {
            return null;
        }
        boolean firstDigitFound = false;
        String number = "";
        for (final Character c : s.toCharArray()) {
            if (!firstDigitFound) {
                if (isNumber(c.toString())) {
                    firstDigitFound = true;
                    number += c;
                }
            } else {
                if (isNumber(c.toString()) || c == '.') {
                    number += c;
                } else {
                    break;
                }
            }
        }
        if (number.isEmpty() || !isNumber(number)) {
            return null;
        }
        return NumberUtils.createDouble(number);
    }

    public static boolean endsWithNumber(String s) {
        return s != null && s.trim().matches(".*\\d");
    }

    public static boolean startsWithNumber(final String s) {
        return s != null && s.trim().matches("\\d.*");
    }

    //    public static boolean startsWithNumber(final String s) {
//        if (s == null) {
//            return false;
//        }
//        return isNumber(String.valueOf(removeSpaces(s).toCharArray()[0]));
//    }

    public static boolean containsNumber(final String s) {
        return s != null && !s.isEmpty() && s.trim().matches(".*\\d.*");
    }

    public static boolean stringEndsWithSubstring(String s, String substring) {
        if (s == null || substring == null || s.isEmpty()) {
            return false;
        }
        s = s.trim().toLowerCase();
        substring = substring.trim().toLowerCase();
        return s.equals(s.replaceFirst(substring, "") + substring);
    }

    public static boolean stringStartsWithSubstring(final String s, final String substring) {
        return s != null && !s.isEmpty() && substring != null
                && s.trim().equals(substring + s.trim().replaceFirst(substring, ""));
    }

    public static boolean isName(final String s) {
        return containsNumber(s) && !startsWithNumber(s);
    }

    public static boolean isSingleWord(final String s) {
        return s != null && !s.isEmpty() && s.trim().equals(s.trim().replaceAll(" ", ""));
    }

    public static boolean isBetweenParenthesises(String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return false;
        }
        final char[] chars = s.toCharArray();
        return chars[0] == '(' && chars[chars.length - 1] == ')';
    }

    public static boolean containsOnlyDigitsAndDash(String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        if (isEmpty(s) && !s.equals("-")) {
            return false;
        }
        return s.matches("\\d*-*\\d*-*\\d*-*\\d*-*\\d*-*\\d*-*");
    }

    public static String sumQuantities(final String c1, final String c2) {
        try {
            final String unit = getUnitFromParameter(c1);
            Double d1 = getFirstNumber(c1);
            Double d2 = getFirstNumber(c2);
            d1 = d1 != null ? d1 : 0;
            d2 = d2 != null ? d2 : 0;
            String sum = sumDoubles(d1, d2);
            if (!isEmpty(unit)) {
                sum = sum + unit;
            }
            return sum;
        } catch (final Exception e) {
            e.printStackTrace();
            return "error value";
        }
    }

    public static String sumDoubles(Double d1, Double d2) {
        d1 = d1 != null ? d1 : 0;
        d2 = d2 != null ? d2 : 0;
        return doubleToString(d1 + d2);
    }

    public static String doubleToString(final Double d) {
        if (d == null) {
            return null;
        }
        return d - d.intValue() == 0 ? String.valueOf(d.intValue()) : String.format("%.01f", d);
    }

}










