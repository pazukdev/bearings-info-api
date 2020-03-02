package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.dto.factory.NestedItemDtoFactory.createBasicNestedItemDto;
import static com.pazukdev.backend.util.CategoryUtil.Category;
import static com.pazukdev.backend.util.CategoryUtil.isPart;

public class NestedItemUtil {

    public static List<NestedItemDto> prepareNestedItemDtosToConverting(final List<NestedItemDto> dtos) {
        final List<NestedItemDto> hasId = new ArrayList<>();
        final List<NestedItemDto> noId = new ArrayList<>();
        for (final NestedItemDto dto : dtos) {
            if (dto.getId() != null) {
                hasId.add(dto);
            } else {
                noId.add(dto);
            }
        }
        List<NestedItemDto> list = removeEqualNestedItems(hasId);
        list.addAll(removeEqualNestedItems(noId));
        return removeEqualNestedItems(list);
    }

    public static Set<NestedItemDto> findEqualNestedItemsInList(final List<NestedItemDto> nestedItems,
                                                                final NestedItemDto checkingNestedItem) {
        final Long checkingNestedItemId = checkingNestedItem.getId();
        final Long checkingNestedItemItemId = checkingNestedItem.getItemId();
        final String checkingNestedItemName = checkingNestedItem.getName();

        final Set<NestedItemDto> equalNestedItems = new HashSet<>();
        for (final NestedItemDto nestedItem : nestedItems) {
            final Long nestedItemId = nestedItem.getId();
            if (nestedItemId != null && nestedItemId.equals(checkingNestedItemId)) {
                equalNestedItems.add(nestedItem);
                continue;
            }

            final Long nestedItemItemId = nestedItem.getItemId();
            final String nestedItemName = nestedItem.getName();
            if (nestedItemItemId.equals(checkingNestedItemItemId)
                    && nestedItemName.equals(checkingNestedItemName)) {
                equalNestedItems.add(nestedItem);
            }
        }
        return equalNestedItems;
    }

    public static List<NestedItemDto> removeEqualNestedItems(final List<NestedItemDto> unfiltered) {
        final List<NestedItemDto> filtered = new ArrayList<>();
        for (final NestedItemDto nestedItem : unfiltered) {
            if (equalNestedItemsAlreadyInList(filtered, nestedItem)) {
                continue;
            }
            if (nestedItem.getId() != null) {
                filtered.add(nestedItem);
                continue;
            }
            NestedItemDto toSave = nestedItem;
            final Set<NestedItemDto> equalOldNestedItems = findEqualNestedItemsInList(unfiltered, nestedItem);
            for (final NestedItemDto dto : equalOldNestedItems) {
                if (dto.getId() != null) {
                    toSave = dto;
                }
            }
            filtered.add(toSave);
        }
        return filtered;
    }

    public static boolean equalNestedItemsAlreadyInList(final List<NestedItemDto> nestedItems,
                                                  final NestedItemDto checkingNestedItem) {
        final Long checkingNestedItemId = checkingNestedItem.getId();
        for (final NestedItemDto nestedItem : nestedItems) {
            final Long nestedItemId = nestedItem.getId();
            if (nestedItemId != null && nestedItemId.equals(checkingNestedItemId)) {
                return true;
            }
            if (nestedItem.getName().equals(checkingNestedItem.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String createName(final String parentItemName, final String nestedItemName) {
        return parentItemName + " - " + nestedItemName;
    }

    public static void addPossiblePartsAndReplacers(final ItemView view,
                                                    final List<Item> allItems,
                                                    final Item parent,
                                                    final List<String> infoCategories,
                                                    final ItemService itemService) {
        final UserService userService = itemService.getUserService();

        for (final Item item : allItems) {
            final String category = item.getCategory();
            if (category.equals(Category.VEHICLE)) {
                continue;
            }

            boolean addPart = isPart(category, infoCategories) && !category.equals(parent.getCategory());
            boolean addReplacer = category.equals(parent.getCategory())
                    || (parent.getCategory().equals("Rubber part") && category.equals("Bearing"));

            NestedItemDto dto = null;
            if (addPart) {
                dto = createBasicNestedItemDto(item, userService);
                view.getPossibleParts().add(dto);
            }
            if (addReplacer) {
                if (dto == null) {
                    dto = createBasicNestedItemDto(item, userService);
                }
                view.getPossibleReplacers().add(dto);
            }
        }
    }

}
