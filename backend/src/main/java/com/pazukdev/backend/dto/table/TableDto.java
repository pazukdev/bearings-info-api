package com.pazukdev.backend.dto.table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@AllArgsConstructor(access = AccessLevel.NONE)
@Data
public class TableDto {

    private String name;
    private String[][] matrix;

    public static TableDto create(final String name, final List<String[]> rows) {
        final TableDto table = new TableDto();
        table.setName(name);
        table.setMatrix(listToMatrix(rows));
        return table;
    }

    private static String[][] listToMatrix(final List<String[]> rows) {
        int j = 0;
        String[][] matrix = new String[rows.size()][];
        for (String[] s : rows) {
            matrix[j++] = s;
        }
        return matrix;
    }

}
