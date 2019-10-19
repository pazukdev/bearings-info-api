package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pazukdev.backend.util.NestedItemUtil.prepareNestedItemDtosToConverting;

public class ChildItemUtil {

    public static List<ChildItem> createParts(final TransitiveItem parent,
                                              final Map<String, String> childItemsDescription,
                                              final ItemService itemService,
                                              final TransitiveItemService transitiveItemService) {
        final List<ChildItem> childItems = new ArrayList<>();
        for (final Map.Entry entry : childItemsDescription.entrySet()) {
            final String category = entry.getKey().toString();
            if (entry.getValue().toString().contains(";")) {
                final String[] names = entry.getValue().toString().split("; ");
                for (final String name : names) {
                    final ChildItem childItem
                            = createChildItem(parent, name, category, itemService, transitiveItemService);
                    if (childItem != null) {
                        childItems.add(childItem);
                    }
                }
            } else {
                final String name = entry.getValue().toString();
                final ChildItem childItem
                        = createChildItem(parent, name, category, itemService, transitiveItemService);
                if (childItem != null) {
                    childItems.add(childItem);
                }
            }
        }

        return childItems;
    }

    public static Set<ChildItem> createPartsFromItemView(final ItemView itemView,
                                                         final ItemService itemService) {
        final List<NestedItemDto> allItems = NestedItemUtil.collectAllItems(itemView.getPartsTable());
        final List<NestedItemDto> preparedItems = prepareNestedItemDtosToConverting(allItems);

        final Set<ChildItem> partsFromItemView = new HashSet<>();
        for (final NestedItemDto nestedItem : preparedItems) {
            final Item partItem = itemService.getOne(nestedItem.getItemId());

            final ChildItem part = new ChildItem();
            part.setId(nestedItem.getId());
            part.setName(nestedItem.getName());
            part.setItem(partItem);
            part.setLocation(nestedItem.getLocation());
            part.setQuantity(nestedItem.getQuantity());
            part.setStatus(nestedItem.getStatus());

            partsFromItemView.add(part);
        }

        return partsFromItemView;
    }

    private static ChildItem createChildItem(final TransitiveItem parent,
                                             final String value,
                                             final String category,
                                             final ItemService itemService,
                                             final TransitiveItemService transitiveItemService) {
        String name;
        String location = "";
        String quantity;
        if (SpecificStringUtil.containsParentheses(value)) {
            name = SpecificStringUtil.getStringBeforeParentheses(value);
            String additionalData = SpecificStringUtil.getStringBetweenParentheses(value);
            location = additionalData.contains(" - ") ? additionalData.split(" - ")[0] : "-";
            quantity = additionalData.contains(" - ") ? additionalData.split(" - ")[1] : additionalData;
        } else {
            name = value;
            location = "-";
            quantity = category.equals("Spark plug") ? "2" : "1";
        }
        final TransitiveItem oldChild = category.equals("Seal")
                ? transitiveItemService.getUssrSealBySize(name)
                : transitiveItemService.find(category, name);

        if (oldChild != null) {
            Item child = itemService.getOrCreate(oldChild);

            final ChildItem childItem = new ChildItem();
            childItem.setName(parent.getName() + " - " + name);
            childItem.setItem(child);
            childItem.setLocation(location);
            childItem.setQuantity(quantity);
            return childItem;
        } else {
            return null;
        }
    }

}
