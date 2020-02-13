package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.ItemQuantity;
import com.pazukdev.backend.dto.ReplacerData;
import com.pazukdev.backend.dto.TransitiveItemDescriptionMap;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.repository.ItemRepository;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.pazukdev.backend.util.CategoryUtil.Category.MANUFACTURER;
import static com.pazukdev.backend.util.CategoryUtil.Category.SEAL;
import static com.pazukdev.backend.util.CategoryUtil.Parameter.SIZE;
import static com.pazukdev.backend.util.CategoryUtil.isAddManufacturer;
import static com.pazukdev.backend.util.CategoryUtil.isInfo;
import static com.pazukdev.backend.util.FileUtil.*;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.UserActionUtil.ActionType.*;
import static com.pazukdev.backend.util.UserActionUtil.processPartAction;
import static com.pazukdev.backend.util.UserActionUtil.processReplacerAction;

/**
 * @author Siarhei Sviarkaltsau
 */
public class ItemUtil {

    public static final String MULTI_PARAM_SEPARATOR = " #";

    @Getter
    public enum SpecialItemId {

        ITEMS_MANAGEMENT_VIEW(-1L),
        MOTORCYCLE_CATALOGUE_VIEW(-2L),
        WISH_LIST_VIEW(-3L),
        USER_LIST_VIEW(-4L);

        private final Long itemId;

        SpecialItemId(final Long itemId) {
            this.itemId = itemId;
        }
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

    public static Set<String> getItemQuantityCategories(final List<ItemQuantity> items) {
        final Set<String> categories = new HashSet<>();
        for (final ItemQuantity item : items) {
            if (item.getItem().getCategory() == null) {
                item.getItem().setCategory("-");
            }
            categories.add(item.getItem().getCategory());
        }
        return categories;
    }

    public static Set<String> getChildItemsCategories(final List<ChildItem> childItems) {
        final Set<String> categories = new HashSet<>();
        for (final ChildItem childItem : childItems) {
            if (childItem.getItem().getCategory() == null) {
                childItem.getItem().setCategory("-");
            }
            categories.add(childItem.getItem().getCategory());
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
                                                                    final TransitiveItemService service,
                                                                    final List<String> infoCategories) {
        final Map<String, String> unsortedMap = toMap(item.getDescription());
        final TransitiveItemDescriptionMap itemDescriptionMap = new TransitiveItemDescriptionMap();
        itemDescriptionMap.setParent(item);
        for (final Map.Entry<String, String> entry : unsortedMap.entrySet()) {
            String parameter = StringUtils.trim(entry.getKey());
            final String value = StringUtils.trim(entry.getValue());
            if (isInfo(parameter, infoCategories)) {
                itemDescriptionMap.getParameters().put(parameter, value);
            } else if (service.isPart(parameter, infoCategories)) {
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

    public static String createButtonText(final Item item, final String manufacturer) {
        if (isAddManufacturer(item, manufacturer, false)) {
            return manufacturer + " " + item.getName();
        } else {
            return item.getName();
        }
    }

    public static String createSelectText(final Item item,
                                          final String manufacturer,
                                          final Map<String, String> descriptionMap) {
        final String itemCategory = item.getCategory();
        final String itemName = item.getName();
        final boolean addManufacturer = isAddManufacturer(item, manufacturer, true);
        if (itemCategory.equals(SEAL)) {
            return descriptionMap.get(SIZE) + " " + (addManufacturer ? manufacturer + " " : "") + itemName;
        }
        if (addManufacturer) {
            return manufacturer + " " + itemName;
        }
        return itemName;
    }

    public static Item getUssrSealBySize(final String searchingSize, final ItemService itemService) {
        final List<Item> ussrSeals = filter(itemService.find(SEAL), MANUFACTURER, "USSR");
        for (Item seal : ussrSeals) {
            final String actualSize = getValueFromDescription(seal.getDescription(), SIZE);
            if (actualSize.equals(searchingSize)) {
                return seal;
            }
        }
        return null;
    }

    public static List<Item> filter(final List<Item> items,
                                    final String parameter,
                                    final String searchingValue) {
        final List<Item> filteredItems = new ArrayList<>();
        for (Item item : items) {
            final String value = getValueFromDescription(item.getDescription(), parameter);
            if (value != null && value.equals(searchingValue)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
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
            final List<String> textLines = getTxtFileTextLines(getTxtFilePath(FileName.COMMENTS));
            for (final String line : new HashSet<>(textLines)) {
                if (line.split("=")[0].equalsIgnoreCase(oldCategory)) {
                    textLines.remove(line);
                    textLines.add(newCategory + "=" + line.split("=")[1]);
                }
            }
            createFile(FileName.COMMENTS, textLines);
        }

        if (renameCategory && infoItem) {
            final List<String> textLines = getTxtFileTextLines(getTxtFilePath(FileName.INFO_CATEGORIES));
            for (final String line : new HashSet<>(textLines)) {
                if (line.equalsIgnoreCase(oldCategory)) {
                    textLines.remove(line);
                    textLines.add(newCategory);
                }
            }
            createFile(FileName.INFO_CATEGORIES, textLines);
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

    public static void updateChildItems(final Item item,
                                        final ItemView itemView,
                                        final ItemService itemService,
                                        final UserEntity user) {
        final Set<ChildItem> oldChildItems = new HashSet<>(item.getChildItems());
        final Set<ChildItem> newChildItems
                = new HashSet<>(ChildItemUtil.createChildrenFromItemView(itemView, itemService));
        item.getChildItems().clear();
        item.getChildItems().addAll(newChildItems);

        for (final ChildItem child : newChildItems) {
            if (child.getId() == null) {
                processPartAction(ADD, child, item, user, itemService);
            }
        }

        final List<ChildItem> toSave = new ArrayList<>();
        for (final ChildItem oldChild : oldChildItems) {
            for (final ChildItem newChild : newChildItems) {
                if (newChild.getName().equals(oldChild.getName())) {
                    toSave.add(oldChild);
                    if (!newChild.getLocation().equals(oldChild.getLocation())
                            || !newChild.getQuantity().equals(oldChild.getQuantity())) {
                        processPartAction(UPDATE, oldChild, item, user, itemService);
                    }
                }
            }
        }

        oldChildItems.removeAll(toSave);

        for (final ChildItem orphan : oldChildItems) {
            itemService.getChildItemRepository().deleteById(orphan.getId());
            processPartAction(DELETE, orphan, item, user, itemService);
        }
    }

    public static void updateReplacers(final Item item,
                                       final ItemView itemView,
                                       final ItemService itemService,
                                       final UserEntity user) {
        final Set<Replacer> oldReplacers = new HashSet<>(item.getReplacers());
        final Set<Replacer> newReplacers =
                new HashSet<>(ReplacerUtil.createReplacersFromItemView(itemView, itemService));
        item.getReplacers().clear();
        item.getReplacers().addAll(newReplacers);

        for (final Replacer replacer : newReplacers) {
            if (replacer.getId() == null) {
                processReplacerAction(ADD, replacer, item, user, itemService);
            }
        }

        final List<Replacer> toSave = new ArrayList<>();
        for (final Replacer oldReplacer : oldReplacers) {
            for (final Replacer newReplacer : newReplacers) {
                if (newReplacer.getName().equals(oldReplacer.getName())) {
                    toSave.add(oldReplacer);
                    if (!newReplacer.getComment().equals(oldReplacer.getComment())) {
                        processReplacerAction(UPDATE, oldReplacer, item, user, itemService);
                    }
                }
            }
        }

        oldReplacers.removeAll(toSave);

        for (final Replacer orphan : oldReplacers) {
            itemService.getReplacerRepository().deleteById(orphan.getId());
            processReplacerAction(DELETE, orphan, item, user, itemService);
        }
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

    public static Set<Long> collectIds(final Set<Item> items) {
        final Set<Long> ids = new HashSet<>();
        for (final Item item : items) {
            ids.add(item.getId());
        }
        return ids;
    }

}


















