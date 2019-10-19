package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.Item;

import java.util.HashSet;
import java.util.Set;

public class CategoryUtil {

    public static boolean isAddManufacturerName(final Item nestedItem) {
        final String category = nestedItem.getCategory();
        return category.equals("Seal") || category.equals("Spark plug");
    }

    public static boolean itemIsAbleToContainParts(final Item item) {
        return !(item.getCategory().equals("Seal")
                || item.getCategory().equals("Spark plug")
                || item.getCategory().equals("Material")
                || item.getCategory().equals("GOST")
                || item.getCategory().equals("Screw")
                || item.getCategory().equals("Lock ring")
                || item.getCategory().equals("Universal joint cross")
                || item.getCategory().equals("Manufacturer")
                || item.getCategory().equals("Oil"));
    }

    public static boolean isPartCategory(final String category) {
        return !(category.equals("Motorcycle")
                || category.equals("Manufacturer")
                || category.equals("Material")
                || category.equals("GOST"));
    }

    public static Set<String> filterPartCategories(final Set<String> categories) {
        final Set<String> partCategories = new HashSet<>();
        for (final String category : categories) {
            if (isPartCategory(category)) {
                partCategories.add(category);
            }
        }
        return partCategories;
    }

}
