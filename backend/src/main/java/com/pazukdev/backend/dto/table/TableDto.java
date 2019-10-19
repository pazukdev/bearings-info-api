package com.pazukdev.backend.dto.table;

import lombok.Data;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class TableDto {

    private final String name;
    private final String[][] matrix;

}
