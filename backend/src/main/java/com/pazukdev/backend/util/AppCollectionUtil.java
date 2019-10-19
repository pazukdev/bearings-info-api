package com.pazukdev.backend.util;

import java.util.List;
import java.util.Objects;

/**
 * @author Siarhei Sviarkaltsau
 */
public class AppCollectionUtil {

    public static List<String[]> toLowerCase(final List<String[]> list) {
        Objects.requireNonNull(list).replaceAll(AppCollectionUtil::toLowerCase);
        return list;
    }

    public static List<String[]> removeSpaces(final List<String[]> list) {
        Objects.requireNonNull(list).replaceAll(AppCollectionUtil::removeSpaces);
        return list;
    }

    private static String[] toLowerCase(final String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].toLowerCase();
        }
        return array;
    }

    private static String[] removeSpaces(final String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = SpecificStringUtil.removeSpaces(array[i]);
        }
        return array;
    }

}
