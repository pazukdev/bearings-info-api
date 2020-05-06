package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;

import java.util.*;

import static com.pazukdev.backend.entity.NestedItem.Type;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.TransitiveItemUtil.findFirstByCategoryAndName;

/**
 * @author Siarhei Sviarkaltsau
 */
public class ChildItemUtil {

    public static List<NestedItem> create(final Type type,
                                          final TransitiveItem parent,
                                          final Map<String, String> childItemsDescription,
                                          final ItemService itemService,
                                          final List<TransitiveItem> transitiveItems,
                                          final List<String> infoCategories,
                                          final UserEntity admin,
                                          final Map<Item, List<NestedItem>> itemsReplacers) {
        final List<NestedItem> items = new ArrayList<>();
        if (type == Type.REPLACER) {
            final String replacersSourceString = parent.getReplacer();
            if (isEmpty(replacersSourceString)) {
                return items;
            }
            if (replacersSourceString == null || replacersSourceString.equals("-")) {
                return items;
            }
            for (final String replacerData : replacersSourceString.split("; ")) {
                String replacerName;
                String comment = null;
                if (containsParentheses(replacerData)) {
                    replacerName = getStringBeforeParentheses(replacerData);
                    comment = getStringBetweenParentheses(replacerData);
                } else {
                    replacerName = replacerData;
                }
                String category = parent.getCategory();
                TransitiveItem transitiveReplacerItem = findFirstByCategoryAndName(category, replacerName, transitiveItems);
                if (transitiveReplacerItem == null && category.equals("Rubber part")) {
                    category = "Bearing";
                    transitiveReplacerItem = findFirstByCategoryAndName(category, replacerName, transitiveItems);
                }
                final Item replacerItem = itemService.convertTransitiveItemToItem(transitiveReplacerItem, transitiveItems, infoCategories, admin, true, itemsReplacers);

                final NestedItem replacer = new NestedItem();
                replacer.setName(NestedItemUtil.createName(parent.getName(), replacerName));
                replacer.setItem(replacerItem);
                replacer.setType(Type.REPLACER.name().toLowerCase());
                if (comment != null) {
                    replacer.setComment(comment);
                }
                items.add(replacer);
            }
        } else {
            for (final Map.Entry<String, String> entry : childItemsDescription.entrySet()) {
                final String category = entry.getKey();
                if (entry.getValue().contains(";")) {
                    final String[] names = entry.getValue().split("; ");
                    for (final String name : names) {
                        final NestedItem child
                                = createPart(parent, transitiveItems, infoCategories, admin, name, category, itemService, itemsReplacers);
                        if (child != null) {
                            items.add(child);
                        }
                    }
                } else {
                    final String name = entry.getValue();
                    final NestedItem child
                            = createPart(parent, transitiveItems, infoCategories, admin, name, category, itemService, itemsReplacers);
                    if (child != null) {
                        items.add(child);
                    }
                }
            }
        }

        for (final NestedItem nestedItem : items) {
            nestedItem.setCreatorId(admin.getId());
        }

        return items;
    }

    private static NestedItem createPart(final TransitiveItem parent,
                                         final List<TransitiveItem> transitiveItems,
                                         final List<String> infoCategories,
                                         final UserEntity admin,
                                         final String value,
                                         final String category,
                                         final ItemService itemService,
                                         final Map<Item, List<NestedItem>> itemsReplacers) {
        String name;
        String comment = "";
        String quantity;
        if (containsParentheses(value)) {
            name = getStringBeforeParentheses(value);
            String additionalData = getStringBetweenParentheses(value);
            comment = additionalData.contains(" - ") ? additionalData.split(" - ")[0] : "-";
            quantity = additionalData.contains(" - ") ? additionalData.split(" - ")[1] : additionalData;
        } else {
            name = value;
            comment = "-";
            quantity = category.equals(CategoryUtil.Category.SPARK_PLUG) ? "2" : "1";
        }

        final TransitiveItem source = TransitiveItemUtil.findFirstByCategoryAndName(category, name, transitiveItems);
        if (source != null) {
            final Item child = itemService.convertTransitiveItemToItem(source, transitiveItems, infoCategories, admin, true, itemsReplacers);

            final NestedItem part = new NestedItem();
            part.setName(getName(parent.getName(), name));
            part.setItem(child);
            part.setComment(comment);
            part.setQuantity(quantity);
            part.setType(Type.PART.name().toLowerCase());
            return part;
        } else {
            return null;
        }
    }

    public static Set<Long> collectIds(final Set<NestedItem> items) {
        final Set<Long> ids = new HashSet<>();
        for (final NestedItem item : items) {
            ids.add(item.getItem().getId());
        }
        return ids;
    }

    public static String createNameForWishListItem(final String itemName) {
        final String parentName = "Wishlist";
        return getName(parentName, itemName);
    }

    public static String getParentName(final Long parentId, final ItemService itemService) {
        if (parentId > 0) {
            return itemService.findOne(parentId).getName();
        }
        if (parentId.equals(ItemUtil.SpecialItemId.WISH_LIST_VIEW.getItemId())) {
            return "Wishlist";
        }
        return null;
    }

    public static String getName(final String parentName, final String name) {
        return parentName + " - " + name;
    }

}
