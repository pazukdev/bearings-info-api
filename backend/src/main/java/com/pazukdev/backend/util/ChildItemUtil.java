package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;

import java.util.*;

import static com.pazukdev.backend.util.SpecificStringUtil.*;

public class ChildItemUtil {

    public static List<ChildItem> createParts(final TransitiveItem parent,
                                              final Map<String, String> childItemsDescription,
                                              final ItemService itemService,
                                              final TransitiveItemService transitiveItemService,
                                              final List<String> infoCategories,
                                              final List<UserEntity> users,
                                              final UserEntity admin) {
        final List<ChildItem> childItems = new ArrayList<>();
        for (final Map.Entry<String, String> entry : childItemsDescription.entrySet()) {
            final String category = entry.getKey();
            if (entry.getValue().contains(";")) {
                final String[] names = entry.getValue().split("; ");
                for (final String name : names) {
                    final ChildItem child
                            = createChild(parent, name, category, itemService, transitiveItemService, infoCategories, users, admin);
                    if (child != null) {
                        childItems.add(child);
                    }
                }
            } else {
                final String name = entry.getValue();
                final ChildItem child
                        = createChild(parent, name, category, itemService, transitiveItemService, infoCategories, users, admin);
                if (child != null) {
                    childItems.add(child);
                }
            }
        }

        return childItems;
    }

    public static Set<ChildItem> createChildrenFromItemView(final ItemView view, final ItemService itemService) {
//        final List<NestedItemDto> preparedItems = prepareNestedItemDtosToConverting(view.getChildren());
        final List<NestedItemDto> preparedItems = view.getChildren();
        final String parentName = getParentName(view, itemService);

        final Set<ChildItem> partsFromItemView = new HashSet<>();
        for (final NestedItemDto nestedItem : preparedItems) {
            final Item partItem = itemService.findOne(nestedItem.getItemId());

            final ChildItem part = new ChildItem();
            part.setId(nestedItem.getId());
            part.setName(getName(parentName, partItem.getName()));
            part.setItem(partItem);
            part.setLocation(nestedItem.getComment());
            part.setQuantity(nestedItem.getSecondComment());
            part.setStatus(nestedItem.getStatus());

            partsFromItemView.add(part);
        }

        return partsFromItemView;
    }

    private static ChildItem createChild(final TransitiveItem parent,
                                         final String value,
                                         final String category,
                                         final ItemService itemService,
                                         final TransitiveItemService transitiveItemService,
                                         final List<String> infoCategories,
                                         final List<UserEntity> users,
                                         final UserEntity admin) {
        String name;
        String location = "";
        String quantity;
        if (containsParentheses(value)) {
            name = getStringBeforeParentheses(value);
            String additionalData = getStringBetweenParentheses(value);
            location = additionalData.contains(" - ") ? additionalData.split(" - ")[0] : "-";
            quantity = additionalData.contains(" - ") ? additionalData.split(" - ")[1] : additionalData;
        } else {
            name = value;
            location = "-";
            quantity = category.equals(CategoryUtil.Category.SPARK_PLUG) ? "2" : "1";
        }

        final TransitiveItem oldChild = transitiveItemService.find(category, name);
        if (oldChild != null) {
            final Item child = itemService.create(oldChild, infoCategories, users, admin);

            final ChildItem childItem = new ChildItem();
            childItem.setName(getName(parent.getName(), name));
            childItem.setItem(child);
            childItem.setLocation(location);
            childItem.setQuantity(quantity);
            return childItem;
        } else {
            return null;
        }
    }

    public static Set<Long> collectIds(final Set<ChildItem> items) {
        final Set<Long> ids = new HashSet<>();
        for (final ChildItem item : items) {
            ids.add(item.getItem().getId());
        }
        return ids;
    }

    public static String createNameForWishListItem(final String itemName) {
        final String parentName = "Wishlist";
        return getName(parentName, itemName);
    }

    public static String getParentName(final ItemView itemView, final ItemService itemService) {
        final Long parentId = itemView.getItemId();
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
