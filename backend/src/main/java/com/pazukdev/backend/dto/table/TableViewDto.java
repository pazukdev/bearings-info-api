package com.pazukdev.backend.dto.table;

import lombok.Data;

import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class TableViewDto {

    private String name;
    private final Integer count;
    private final List<TableDto> tables;

}
