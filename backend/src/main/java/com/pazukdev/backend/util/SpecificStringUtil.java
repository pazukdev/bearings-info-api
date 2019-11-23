package com.pazukdev.backend.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
public class SpecificStringUtil {

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

    private static final List<String> nullKeys = Arrays.asList("?", "-", "null");
    private static final List<String> abbreviation = Arrays.asList("имз", "кмз", "гост");

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

    public static String capitalize(final String s) {
        if (isAbbreviation(s)) {
            return s.toUpperCase();
        }
        return StringUtils.capitalize(s.toLowerCase());
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
        return abbreviation.contains(s);
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

    public static String replaceBlankWithDash(final String s) {
        return StringUtils.isBlank(s) ? "-" : s;
    }

}










