package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;

import java.util.*;

import static com.pazukdev.backend.dto.factory.NestedItemDtoFactory.*;

public class TableUtil {

    public static PartsTable specialItemsTable(final List<Item> items,
                                               final String tableName,
                                               final ItemService itemService) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        for (final Item item : items) {
            final NestedItemDto itemDto = createItemForItemsManagement(item, itemService.getUserService());
            dtos.add(itemDto);
        }
//        final String[] header = {"Category", "Name", "-"};
        final String[] header = null;
        final Set<String> categories = itemService.findAllCategories();
        final PartsTable partsTable =  PartsTable.create(dtos, tableName, header, categories);

        for (final PartsTable childTable : partsTable.getTables()) {
            childTable.setOpened(false);
        }

        return partsTable;
    }

    public static  PartsTable motorcyclesTable(final List<Item> motorcycles,
                                               final String tableName,
                                               final UserService userService) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        final Set<String> categories = new HashSet<>();
        for (final Item motorcycle : motorcycles) {
            final NestedItemDto motorcycleDto = createMotorcycle(motorcycle, userService);
            dtos.add(motorcycleDto);
            categories.add(motorcycleDto.getItemCategory());
        }

        final String[] header = {"Production", "Model", "Manufacturer"};
        final PartsTable partsTable =  PartsTable.create(dtos, tableName, header, categories);

        for (final PartsTable child : partsTable.getTables()) {
            child.setOpened(child.getWeight() > 7);
        }

        return partsTable;
    }

    public static  PartsTable usersTable(final List<UserEntity> users, final String tableName) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        for (final UserEntity user : users) {
            dtos.add(createUser(user));
        }
        final String[] header = {"Role", "Username", "Rating"};
        final Set<String> partCategories = new HashSet<>(Arrays.asList("Admin", "User"));
        final PartsTable partsTable =  PartsTable.create(dtos, tableName, header, partCategories);
        for (final PartsTable child : partsTable.getTables()) {
            child.getParts().sort(Comparator.comparing(NestedItemDto::getQuantity));
        }
        return partsTable;
    }

    public static PartsTable createPartsTable(final Item item,
                                              final String tableName,
                                              final ItemService itemService) {
        if (!CategoryUtil.itemIsAbleToContainParts(item)) {
            return PartsTable.createStub();
        }
        final List<NestedItemDto> dtos = new ArrayList<>();
        final List<ChildItem> parts = new ArrayList<>(item.getChildItems());
        for (final ChildItem part : parts) {
            final NestedItemDto partDto = createChildItem(part, itemService.getUserService());
            dtos.add(partDto);
        }
        final String[] header = {"Location", "Partnumber", "Pcs/Vol"};
        //final String[] header = null;
        final Set<String> categories = itemService.findAllPartCategories();
        return PartsTable.create(dtos, tableName, header, categories);
    }

    public static ReplacersTable createReplacersTable(final Item item, final UserService userService) {
        final ReplacersTable replacersTable = new ReplacersTable();
        replacersTable.setName("Replacers");
        final List<Replacer> replacers = new ArrayList<>(item.getReplacers());
        for (final Replacer replacer : replacers) {
            final NestedItemDto replacerDto = createReplacer(replacer, userService);
            replacersTable.getReplacers().add(replacerDto);
        }
        replacersTable.getReplacers().sort(Comparator.comparing(NestedItemDto::getRating).reversed());
        return replacersTable;
    }

    public static HeaderTable createHeader(final Item item, final ItemService itemService) {
        final String itemName = item.getName();
        final String itemCategory = item.getCategory();
        final String tableName = getHeaderTableName(itemCategory, itemName);
        final Map<String, String> description = ItemUtil.toMap(item.getDescription());

        final List<HeaderTableRow> headerTableRows = new ArrayList<>();
        headerTableRows.add(HeaderTableRow.create("Name", itemName, itemCategory));
        return createTable(tableName, description, headerTableRows, itemCategory, itemService);
    }

    public static String getHeaderTableName(final String itemCategory, final String itemName) {
        return itemCategory + " " + itemName;
    }

    private static HeaderTable createTable(final String tableName,
                                           final Map<String, String> descriptionMap,
                                           final List<HeaderTableRow> headerTableRows,
                                           final String itemCategory,
                                           final ItemService itemService) {
        for (final Map.Entry<String, String> entry : descriptionMap.entrySet()) {
            String parameter = entry.getKey();
            String value = entry.getValue();
            String itemId = "no id";

            final Item foundItem = itemService.find(parameter, value);
            if (foundItem != null) {
                itemId = foundItem.getId().toString();
            }

            final HeaderTableRow row = HeaderTableRow.create(parameter, value, itemCategory);
            row.setItemId(itemId);
            headerTableRows.add(row);
        }
        return HeaderTable.create(tableName, headerTableRows);
    }

    public static Map<String, String> createHeaderMap(final HeaderTable header) {
        final Map<String, String> map = new HashMap<>();
        for (final HeaderTableRow row : header.getRows()) {
            map.put(row.getParameter(), row.getValue());
        }
        return map;
    }

    private static boolean isBoxer(final Item motorcycle) {
        final String description = motorcycle.getDescription().toLowerCase();
        final String type = ItemUtil.getValueFromDescription(description, "type");
        final String manufacturer = ItemUtil.getValueFromDescription(description, "manufacturer");
        final String name = motorcycle.getName().toLowerCase();

        if (type != null && type.equals("boxer")) {
            return true;
        } else if (manufacturer != null) {
            if (manufacturer.contains("imz") || manufacturer.contains("kmz")
                    || (manufacturer.equals("bmw") && name.equals("r75"))
                    || (manufacturer.equals("zundapp") && name.equals("ks750"))) {
                return true;
            }
        }
        return false;
    }

}
