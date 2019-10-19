package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.dto.NestedItemDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PartsTable extends AbstractDto {

    private String[] header = {"-", "-", "-"};
    private List<NestedItemDto> parts = new ArrayList<>();
    private List<PartsTable> tables = new ArrayList<>();

    public static PartsTable create(final List<NestedItemDto> nestedItems,
                                    final String tableName,
                                    final String[] header,
                                    final Set<String> partCategories) {
        final PartsTable partsTable = new PartsTable();
        partsTable.setName(tableName);
        if (header != null) {
            partsTable.setHeader(header);
        }
        for (final String category : partCategories) {
            final PartsTable categoryTable = new PartsTable();
            categoryTable.setName(category);
            partsTable.getTables().add(categoryTable);
        }
        for (final NestedItemDto nestedItem : nestedItems) {
            addToCategoryTable(partsTable.getTables(), nestedItem);
        }

        for (final PartsTable childTable : partsTable.getTables()) {
            childTable.getParts().sort(Comparator.comparing(NestedItemDto::getQuantity).reversed());
        }

        partsTable.getTables().sort(Comparator.comparing(PartsTable::getName));
        return partsTable;
    }

    public static void addToCategoryTable(final List<PartsTable> categoryTables,
                                          final NestedItemDto nestedItem) {
        for (final PartsTable categoryTable : categoryTables) {
            if (categoryTable.getName().toLowerCase().equals(nestedItem.getItemCategory().toLowerCase())) {
                categoryTable.getParts().add(nestedItem);
            }
        }
    }

    public static PartsTable createSingleCategoryTable(final List<NestedItemDto> categorizedItems) {
        final PartsTable partsTable = new PartsTable();
        partsTable.setName(categorizedItems.get(0).getItemCategory());
        partsTable.getParts().addAll(categorizedItems);
        return partsTable;
    }

}
