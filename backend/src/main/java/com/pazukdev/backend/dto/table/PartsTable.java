package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.dto.NestedItemDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
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
//    private Integer weight = 0;
    private List<NestedItemDto> parts = new ArrayList<>();
    private Set<String> partCategories = new HashSet<>();
//    private List<PartsTable> tables = new ArrayList<>();
    private boolean opened = true;

    public static PartsTable createStub() {
        final PartsTable partsTable = new PartsTable();
        partsTable.setName("stub");
        return partsTable;
    }

    public static PartsTable create(final String[] header,
                                    final List<NestedItemDto> parts,
                                    final Set<String> partCategories) {
        final PartsTable table = new PartsTable();
        table.setHeader(header);
        table.setParts(parts);
        table.setPartCategories(partCategories);
        return table;
    }

//    public static PartsTable create(final List<NestedItemDto> nestedItems,
//                                    final String tableName,
//                                    final String[] header,
//                                    final Set<String> partCategories) {
//
//        final PartsTable partsTable = new PartsTable();
//        partsTable.setName(tableName);
//
//        if (header != null) {
//            partsTable.setHeader(header);
//        }
//
//        for (final String partCategory : partCategories) {
//            final PartsTable categoryTable = new PartsTable();
//            categoryTable.setName(partCategory);
//            setTableWeight(categoryTable, partCategory);
//            partsTable.getTables().add(categoryTable);
//        }
//        for (final NestedItemDto nestedItem : nestedItems) {
//            addToCategoryTable(partsTable.getTables(), nestedItem);
//        }
//
//        sortItemsInChildTables(partsTable);
//        sortChildTables(partsTable);
//        return partsTable;
//    }
//
//    public static void sortItemsInChildTables(final PartsTable parent) {
//        for (final PartsTable childTable : parent.getTables()) {
//            childTable.getParts().sort(Comparator
//                    .comparing(NestedItemDto::getLocation)
//                    .thenComparing(NestedItemDto::getLocalizedComment)
//                    .thenComparing(NestedItemDto::getButtonText));
//        }
//    }
//
//    public static void sortChildTables(final PartsTable parent) {
//        parent.getTables().sort(Comparator
//                .comparing(PartsTable::getWeight).reversed()
//                .thenComparing(PartsTable::getLocalizedName));
//    }
//
//    private static void setTableWeight(final PartsTable categoryTable, final String partCategory) {
//        if (partCategory.toLowerCase().equals("kmz")) {
//            categoryTable.setWeight(10);
//        }
//        if (partCategory.toLowerCase().contains("kmz") && partCategory.toLowerCase().contains("imz")) {
//            categoryTable.setWeight(9);
//        }
//        if (partCategory.toLowerCase().equals("imz")) {
//            categoryTable.setWeight(8);
//        }
//        if (partCategory.toLowerCase().equals("bmw")) {
//            categoryTable.setWeight(7);
//        }
//        if (partCategory.toLowerCase().equals("zundapp")) {
//            categoryTable.setWeight(6);
//        }
//    }

    private static void addToCategoryTable(final List<PartsTable> categoryTables,
                                           final NestedItemDto nestedItem) {
        for (final PartsTable categoryTable : categoryTables) {
            if (categoryTable.getName().toLowerCase().equals(nestedItem.getItemCategory().toLowerCase())) {
                categoryTable.getParts().add(nestedItem);
            }
        }
    }

}
