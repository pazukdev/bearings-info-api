package com.pazukdev.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static boolean contains(final List<String> list, final String element) {
        return list.stream().anyMatch(element::equalsIgnoreCase);
    }

    public static List<List<String>> listOfArraysToListOfLists(final List<String[]> arrays) {
        final List<List<String>> lists = new ArrayList<>();
        for (final String[] array : arrays) {
            lists.add(new ArrayList<>(Arrays.asList(array)));
        }
        return lists;
    }

}
