package com.pazukdev.backend.tablemodel;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TableModelImpl implements TableModel {

    private final List<TableRow> tableRows;

}
