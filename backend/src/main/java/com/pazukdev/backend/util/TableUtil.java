package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.factory.NestedItemDtoFactory;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pazukdev.backend.util.CategoryUtil.Parameter;
import static com.pazukdev.backend.util.SpecificStringUtil.fixParam;
import static com.pazukdev.backend.util.SpecificStringUtil.fixValue;

public class TableUtil {

    public static ReplacersTable createReplacersTable(final Item item, final UserService userService) {
        final ReplacersTable replacersTable = new ReplacersTable();
        replacersTable.setName("Replacers");
        final List<NestedItem> replacers = new ArrayList<>(item.getReplacers());
        for (final NestedItem replacer : replacers) {
            final NestedItemDto replacerDto = NestedItemDtoFactory.createChild(replacer, userService, true);
            replacersTable.getReplacers().add(replacerDto);
        }
        return replacersTable;
    }

    public static HeaderTable createHeader(final Item item,
                                           final Map<String, String> description,
                                           final ItemService service) {
        final String itemName = item.getName();
        final String itemCategory = item.getCategory();
        final String tableName = getHeaderTableName(itemCategory, itemName);

        final List<HeaderTableRow> headerTableRows = new ArrayList<>();
        headerTableRows.add(HeaderTableRow.create(Parameter.DescriptionIgnored.NAME, itemName, service));
        headerTableRows.add(HeaderTableRow.create(Parameter.DescriptionIgnored.CATEGORY, itemCategory, service));
        return createTable(tableName, description, headerTableRows, service);
    }

    public static String getHeaderTableName(final String itemCategory, final String itemName) {
        return itemCategory + " " + itemName;
    }

    private static HeaderTable createTable(final String tableName,
                                           final Map<String, String> descriptionMap,
                                           final List<HeaderTableRow> rows,
                                           final ItemService service) {
        for (final Map.Entry<String, String> entry : descriptionMap.entrySet()) {
            rows.add(HeaderTableRow.create(entry.getKey(), entry.getValue(), service));
        }
        return HeaderTable.create(tableName, rows);
    }

    public static Map<String, String> createHeaderMap(final HeaderTable header) {
        final Map<String, String> map = new HashMap<>();
        for (final HeaderTableRow row : header.getRows()) {
            map.put(fixParam(row.getParameter()), fixValue(row.getValue()));
        }
        return map;
    }

}
