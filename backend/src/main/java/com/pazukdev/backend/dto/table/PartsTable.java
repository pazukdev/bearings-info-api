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
    private Integer weight = 0;
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
            setTableWeight(categoryTable, category);
            partsTable.getTables().add(categoryTable);
        }
        for (final NestedItemDto nestedItem : nestedItems) {
            addToCategoryTable(partsTable.getTables(), nestedItem);
        }

        for (final PartsTable childTable : partsTable.getTables()) {
            childTable.getParts().sort(Comparator.comparing(NestedItemDto::getQuantity).reversed());
        }

        sortChildTables(partsTable);
        return partsTable;
    }

    private static void setTableWeight(final PartsTable categoryTable, final String category) {
        if (category.equals("IMZ")) {
            categoryTable.setWeight(1);
        }
        if (category.equals("IMZ / KMZ")) {
            categoryTable.setWeight(2);
        }
        if (category.equals("KMZ")) {
            categoryTable.setWeight(3);
        }
    }

    private static void sortChildTables(final PartsTable parentTable) {
        final List<PartsTable> zeroWeight = collectTablesWithZeroWeight(parentTable);
        zeroWeight.sort(Comparator.comparing(PartsTable::getName));
        parentTable.getTables().removeAll(zeroWeight);
        parentTable.getTables().sort(Comparator.comparing(PartsTable::getWeight));
        parentTable.getTables().addAll(zeroWeight);
    }

    private static List<PartsTable> collectTablesWithZeroWeight(final PartsTable parentTable) {
        final List<PartsTable> zeroWeight = new ArrayList<>();
        for (final PartsTable childTable : parentTable.getTables()) {
            if (childTable.getWeight() == 0) {
                zeroWeight.add(childTable);
            }
        }
        return zeroWeight;
    }

    private static void addToCategoryTable(final List<PartsTable> categoryTables,
                                           final NestedItemDto nestedItem) {
        for (final PartsTable categoryTable : categoryTables) {
            if (categoryTable.getName().toLowerCase().equals(nestedItem.getItemCategory().toLowerCase())) {
                categoryTable.getParts().add(nestedItem);
            }
        }
    }

}
