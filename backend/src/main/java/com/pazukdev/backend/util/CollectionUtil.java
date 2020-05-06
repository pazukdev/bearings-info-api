package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.abstraction.AbstractEntity;

import java.util.*;

/**
 * @author Siarhei Sviarkaltsau
 */
public class CollectionUtil {

    public static List<String[]> toLowerCase(final List<String[]> list) {
        Objects.requireNonNull(list).replaceAll(CollectionUtil::toLowerCase);
        return list;
    }

    public static List<String[]> removeSpaces(final List<String[]> list) {
        Objects.requireNonNull(list).replaceAll(CollectionUtil::removeSpaces);
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

    public static <T extends AbstractEntity> List<T> findAllById(final List<Long> ids, final List<T> toSearchIn) {
        final List<T> found = new ArrayList<>();
        for (final T entity : toSearchIn) {
            if (ids.contains(entity.getId())) {
                found.add(entity);
            }
        }
        return found;
    }

    public static <T extends AbstractEntity> T findFirstByName(final String name, final List<T> entities) {
        return entities.stream().filter(entity -> entity.getName().equals(name)).findFirst().orElse(null);
    }

    public static <T extends AbstractEntity> Set<Long> collectIds(final Set<T> entities) {
        final Set<Long> ids = new HashSet<>();
        for (final T entity : entities) {
            ids.add(entity.getId());
        }
        return ids;
    }

    public static void removeAllEmpty(final List<String> list) {
        list.removeIf(SpecificStringUtil::isEmpty);
    }

}
