package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderTable extends AbstractDto {

    private List<HeaderTableRow> rows;

    public String getValue(final String param) {
        for (final HeaderTableRow row : rows) {
            if (row.getParameter().equals(param)) {
                return row.getValue();
            }
        }
        return null;
    }

    public void removeRow(final String param) {
        rows.removeIf(row -> row.getParameter().equals(param));
    }

    public static HeaderTable create(final String tableName, final List<HeaderTableRow> rows) {
        final HeaderTable headerTable = new HeaderTable();
        headerTable.setName(tableName);
        headerTable.setRows(rows);
        return headerTable;
    }

    public static HeaderTable createSingleRowTable(final String tableName, final HeaderTableRow row) {
        final HeaderTable headerTable = new HeaderTable();
        headerTable.setName(tableName);
        headerTable.setRows(new ArrayList<>(Collections.singletonList(row)));
        return headerTable;
    }

}
