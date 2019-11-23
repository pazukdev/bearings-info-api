package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.factory.NestedItemDtoFactory;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class NestedItemUtil {

    public static List<NestedItemDto> prepareNestedItemDtosToConverting(final List<NestedItemDto> dtos) {
        correctFieldsValues(dtos);

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

    public static void correctFieldsValues(final List<NestedItemDto> dtos) {
        for (final NestedItemDto dto : dtos) {
            final String comment = dto.getComment();
            final String location = dto.getLocation();
            final String quantity = dto.getQuantity();
            if (StringUtils.isBlank(comment)) {
                dto.setComment("-");
            }
            if (StringUtils.isBlank(location)) {
                dto.setLocation("-");
            }
            if (StringUtils.isBlank(quantity)) {
                dto.setQuantity("0");
            }
        }
    }

    public static List<List<NestedItemDto>> categorize(final List<NestedItemDto> nestedItems) {
        final List<List<NestedItemDto>> categorizedItems = new ArrayList<>();
        for (final String category : getCategories(nestedItems)) {
            categorizedItems.add(nestedItems.stream().filter(
                    nestedItem -> nestedItem.getItemCategory().equals(category)).collect(Collectors.toList()));

        }
        return categorizedItems;
    }

    public static Set<String> getCategories(final List<NestedItemDto> nestedItems) {
        final Set<String> categories = new HashSet<>();
        for (final NestedItemDto nestedItem : nestedItems) {
            categories.add(nestedItem.getItemCategory());
        }
        return categories;
    }

    public static String createName(final String parentItemName, final String nestedItemName) {
        return parentItemName + " - " + nestedItemName;
    }

    public static List<NestedItemDto> collectAllItems(final PartsTable partsTable) {
        final Set<NestedItemDto> allItems = new HashSet<>(partsTable.getParts());
        for (final PartsTable childTable : partsTable.getTables()) {
            allItems.addAll(childTable.getParts());
        }
        return new ArrayList<>(allItems);
    }

    public static List<NestedItemDto> createPossibleParts(final List<Item> items, final UserService userService) {
        final List<NestedItemDto> childItemDtos = new ArrayList<>();
        for (final Item item : items) {
            final String category = item.getCategory();
            if (!CategoryUtil.isPartCategory(category)) {
                continue;
            }
            final NestedItemDto dto = NestedItemDtoFactory.createBasicNestedItemDto(item, userService);
            dto.setSelectText(item.getCategory() + " " + dto.getButtonText());
            childItemDtos.add(dto);
        }
        return childItemDtos;
    }

    public static List<NestedItemDto> createReplacerDtos(final List<Item> items, final UserService userService) {
        final List<NestedItemDto> replacerDtos = new ArrayList<>();
        for (final Item item : items) {
            replacerDtos.add(NestedItemDtoFactory.createBasicNestedItemDto(item, userService));
        }
        replacerDtos.sort(Comparator.comparing(NestedItemDto::getRating).reversed());
        return replacerDtos;
    }

}
