package com.pazukdev.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class ItemData implements Serializable {

    private final static long serialVersionUID = 12343L;

    private String name;
    private String localizedName;

    public static ItemData create(final String name) {
        final ItemData itemData = new ItemData();
        itemData.setName(name);
        itemData.setLocalizedName(name);
        return itemData;
    }

//    public static List<ItemData> findAllCategories(final ItemService itemService) {
//        final List<ItemData> categories = new ArrayList<>();
//        for (final String category : itemService.findAllCategories()) {
//            categories.add(ItemData.create(category));
//        }
//        categories.sort(Comparator.comparing(ItemData::getLocalizedName));
//        return categories;
//    }

}
