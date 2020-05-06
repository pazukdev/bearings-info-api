package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.TransitiveItem;

import java.util.List;

import static com.pazukdev.backend.util.CategoryUtil.isInfo;

/**
 * @author Siarhei Sviarkaltsau
 */
public class TransitiveItemUtil {

    public static TransitiveItem findFirstByCategory(final String category, final List<TransitiveItem> items) {
        return items.stream().filter(item -> item.getCategory().equals(category)).findFirst().orElse(null);
    }

    public static TransitiveItem findFirstByCategoryAndName(final String category,
                                                            final String name,
                                                            final List<TransitiveItem> items) {
        return items.stream()
                .filter(item -> item.getCategory().equals(category) && item.getName().equals(name)).findFirst()
                .orElse(null);
    }

    public static boolean isPart(final String parameter,
                          final List<TransitiveItem> items,
                          final List<String> infoCategories) {
        if (isInfo(parameter, infoCategories)) {
            return false;
        }
        return findFirstByCategory(parameter, items) != null;
    }

}




















