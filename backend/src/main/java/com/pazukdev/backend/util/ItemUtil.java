package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.ReplacerData;
import com.pazukdev.backend.dto.TransitiveItemDescriptionMap;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.repository.ItemRepository;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.pazukdev.backend.converter.NestedItemConverter.convert;
import static com.pazukdev.backend.util.CategoryUtil.*;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.UserActionUtil.createActions;

/**
 * @author Siarhei Sviarkaltsau
 */
public class ItemUtil {

    @Getter
    public enum SpecialItemId {

        ITEMS_MANAGEMENT_VIEW,
        VEHICLES_VIEW,
        WISH_LIST_VIEW,
        USER_LIST_VIEW

    }

    public static Set<String> findCategories(final List<TransitiveItem> items) {
        final Set<String> categories = new HashSet<>();
        for (final TransitiveItem item : items) {
            if (item.getCategory() == null) {
                item.setCategory("-");
            }
            categories.add(item.getCategory());
        }
        return categories;
    }

    public static List<List<TransitiveItem>> categorize(final List<TransitiveItem> items) {
        final List<List<TransitiveItem>> categorizedItems = new ArrayList<>();
        for (final String category : findCategories(items)) {
            categorizedItems.add(items.stream()
                    .filter(item -> item.getCategory().equals(category)).collect(Collectors.toList()));

        }
        return categorizedItems;
    }

    public static String getValueFromDescription(final TransitiveItem item, final String key) {
        return getValueFromDescription(item.getDescription(), key);
    }

    public static String getValueFromDescription(final String description, final String key) {
        return toMap(description).get(key);
    }

    public static String toDescription(final Map<String, String> map) {
        String description = "";
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("Name")) {
                continue;
            }
            description += entry.getKey() + ":" + entry.getValue() + ";;";
        }
        return description;
    }

    public static String toDescription(final TransitiveItemDescriptionMap descriptionMap) {
        final String description
                = toDescription(descriptionMap.getParameters())
                + toDescription(descriptionMap.getSelectableParameters())
                + toDescription(descriptionMap.getItems());
        return replaceEmptyWithDash(description);
    }

    public static Map<String, String> toMap(final String description) {
        final Map<String, String> map = new HashMap<>();
        if (description == null || !description.contains(":")) {
            return map;
        }
        final String[] descriptionList = description.split(";;");
        for (final String element : descriptionList) {
            map.put(element.split(":")[0], element.split(":")[1]);
        }
        return map;
    }

    public static TransitiveItemDescriptionMap createDescriptionMap(final TransitiveItem item,
                                                                    final List<TransitiveItem> transitiveItems,
                                                                    final List<String> infoCategories) {
        final Map<String, String> unsortedMap = toMap(item.getDescription());
        final TransitiveItemDescriptionMap itemDescriptionMap = new TransitiveItemDescriptionMap();
        itemDescriptionMap.setParent(item);
        for (final Map.Entry<String, String> entry : unsortedMap.entrySet()) {
            String parameter = StringUtils.trim(entry.getKey());
            final String value = StringUtils.trim(entry.getValue());
            if (isInfo(parameter, infoCategories)) {
                itemDescriptionMap.getParameters().put(parameter, value);
            } else if (TransitiveItemUtil.isPart(parameter, transitiveItems, infoCategories)) {
                itemDescriptionMap.getItems().put(parameter, value);
            } else {
                itemDescriptionMap.getParameters().put(parameter, value);
            }
        }
        return itemDescriptionMap;
    }

    public static ReplacerData parseReplacerData(final String replacerDataSourceString) {
        final ReplacerData replacerData = new ReplacerData();
        String replacerName;
        String comment = "-";
        if (containsParentheses(replacerDataSourceString)) {
            replacerName = getStringBeforeParentheses(replacerDataSourceString);
            comment = getStringBetweenParentheses(replacerDataSourceString);
        } else {
            replacerName = replacerDataSourceString;
        }
        replacerData.setName(replacerName);
        replacerData.setComment(comment);
        return replacerData;
    }

    public static List<ReplacerData> parseReplacersSourceString(final String replacersSourceString) {
        final List<ReplacerData> data = new ArrayList<>();
        if (!replacersSourceString.equals("-")) {
            for (final String replacerData : replacersSourceString.split("; ")) {
                data.add(parseReplacerData(replacerData));
            }
        }
        return data;
    }

    public static String createButtonText(final Item item,
                                          final String manufacturer,
                                          final String partNumber) {
        String itemName = item.getName();
        if (isAddManufacturer(item, manufacturer, false)) {
            final String manufacturerText = manufacturer.replaceAll("KMZ; IMZ", "IMZ; KMZ").replaceAll("; ", " / ");
            if (!itemName.contains(manufacturerText)) {
                itemName = manufacturerText + " " + itemName;
            }
        }
        final String category = item.getCategory();
        if (category.equals(Category.SEAL) || category.equals(Category.LOCK_RING)) {
            final String size = ItemUtil.getValueFromDescription(item.getDescription(), "Size, mm");
            if (size != null && !size.equals(item.getName())) {
                itemName = size + "=" + itemName;
            }
        } else if (category.equals(Category.ROLLING_ELEMENT)) {
            final String type = ItemUtil.getValueFromDescription(item.getDescription(), "Type");
            if (type != null && !type.equals(item.getName()) && !item.getName().contains(type)) {
                itemName = type + " = " + itemName.replaceAll("Roller ", "");
            }
        }
        if (!isEmpty(partNumber) && !partNumber.equals(item.getName())) {
            itemName = itemName + " " + partNumber;
        }
        return itemName;
    }

    public static boolean updateNameAndCategory(final Item item,
                                                final String newCategory,
                                                final String newName,
                                                final List<Item> allItems,
                                                final List<String> infoCategories,
                                                final ItemService service) {
        final String oldName = item.getName();
        final String oldCategory = item.getCategory();

        boolean nameChanged = newName != null && !newName.equals(oldName);
        boolean categoryChanged = newCategory != null && !newCategory.equals(oldCategory);

        if (!nameChanged && !categoryChanged) {
            return false;
        }

        boolean infoItem = isInfo(oldCategory, infoCategories);
        boolean checkDescriptions = nameChanged && infoItem;
        boolean renameCategory = categoryChanged && !service.collectCategories(allItems).contains(newCategory);
        boolean moveItemToAnotherCategory = categoryChanged && !renameCategory;

        if (nameChanged) {
            item.setName(newName);
        }
        if (categoryChanged && !moveItemToAnotherCategory) {
            item.setCategory(newCategory);
        }

        if (checkDescriptions || renameCategory) {
            for (final Item i : allItems) {
                boolean descriptionChanged = false;
                final Map<String, String> descriptionMap = toMap(i.getDescription());
                if (checkDescriptions) {
                    for (final Map.Entry<String, String> entry : descriptionMap.entrySet()) {
                        if (entry.getValue().equals(oldName)) {
                            entry.setValue(newName);
                            descriptionChanged = true;
                        }
                    }
                }
                if (renameCategory && infoItem) {
                    final String value = descriptionMap.get(oldCategory);
                    if (value != null) {
                        descriptionMap.remove(oldCategory);
                        descriptionMap.put(newCategory, value);
                        descriptionChanged = true;
                    }
                }

                final boolean setNewCategory = i.getCategory().equals(oldCategory) && renameCategory;
                if (setNewCategory) {
                    i.setCategory(newCategory);
                }
                if (descriptionChanged) {
                    i.setDescription(toDescription(descriptionMap));
                }
                if (descriptionChanged || setNewCategory) {
                    service.update(i);
                }

            }
        }

        if (renameCategory) {
            final List<String> textLines = FileUtil.getComments();
            for (final String line : new HashSet<>(textLines)) {
                if (line.split("=")[0].equalsIgnoreCase(oldCategory)) {
                    textLines.remove(line);
                    textLines.add(newCategory + "=" + line.split("=")[1]);
                }
            }
        }

        if (renameCategory && infoItem) {
            final List<String> textLines = FileUtil.getInfoCategories();
            for (final String line : new HashSet<>(textLines)) {
                if (line.equalsIgnoreCase(oldCategory)) {
                    textLines.remove(line);
                    textLines.add(newCategory);
                }
            }
        }

        return moveItemToAnotherCategory;
    }

    public static String createEmptyDescription(final String category, final ItemRepository repository) {
        final Item firstFound = repository.findFirstByCategory(category);
        if (firstFound == null) {
            return "";
        }
        final Map<String, String> descriptionMap = ItemUtil.toMap(firstFound.getDescription());
        for (final Map.Entry<String, String> entry : descriptionMap.entrySet()) {
            entry.setValue("-");
        }
        return ItemUtil.toDescription(descriptionMap);
    }

    public static void updateNestedItems(final Item parent,
                                         final ItemView view,
                                         final UserEntity user,
                                         final ItemService service,
                                         final List<UserAction> actions) {

        final Long parentId = parent.getId();

        final Set<NestedItem> oldParts = new HashSet<>(parent.getParts());
        final Set<NestedItem> oldReplacers = new HashSet<>(parent.getReplacers());

        List<NestedItemDto> partDtos = view.getChildren();
        List<NestedItemDto> replacerDtos = view.getReplacersTable().getReplacers();

        Set<NestedItem> newParts = new HashSet<>(convert(partDtos, parentId, service, user));
        Set<NestedItem> newReplacers = new HashSet<>(convert(replacerDtos, parentId, service, user));

        parent.getParts().clear();
        parent.getReplacers().clear();

        parent.getParts().addAll(newParts);
        parent.getReplacers().addAll(newReplacers);

        actions.addAll(createActions(parent, oldParts, newParts, user, service));
        actions.addAll(createActions(parent, oldReplacers, newReplacers, user, service));
    }

    public static void moveItemToAnotherCategory(final Item item,
                                                 final String newCategory,
                                                 final Map<String, String> newDescriptionMap,
                                                 final ItemService service) {
        final Map<String, String> emptyMap = toMap(createEmptyDescription(newCategory, service.getItemRepository()));
        for (Map.Entry<String, String> entry : emptyMap.entrySet()) {
            final String oldValue = newDescriptionMap.get(entry.getKey());
            if (oldValue != null) {
                entry.setValue(oldValue);
            }
        }
        item.setDescription(toDescription(emptyMap));
        item.setCategory(newCategory);
    }

    public static void applyNewDescriptionToCategory(final String category,
                                                     final HeaderTable headerTable,
                                                     final Map<String, String> newDescriptionMap,
                                                     final List<Item> allItems,
                                                     final ItemService itemService) {

        for (final Item item : itemService.find(category, allItems)) {
            final Map<String, String> oldDescriptionMap = toMap(item.getDescription());
            for (final HeaderTableRow row : headerTable.getRows()) {
                final String oldParam = row.getName();
                final String newParam = row.getParameter();
                final String oldValue = oldDescriptionMap.get(oldParam);
                boolean paramChanged = !oldParam.equals(newParam);

                if (paramChanged) {
                    newDescriptionMap.put(newParam, oldValue == null ? "-" : oldValue);
                } else {
                    newDescriptionMap.remove(newParam);
                    newDescriptionMap.put(newParam, oldValue);
                }
            }
            item.setDescription(toDescription(newDescriptionMap));
        }
    }

}


















