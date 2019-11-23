package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.dto.NestedItemDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReplacersTable extends AbstractDto {

    private List<NestedItemDto> replacers = new ArrayList<>();

    public static ReplacersTable createStub() {
        final ReplacersTable replacersTable = new ReplacersTable();
        replacersTable.setName("stub");
        return replacersTable;
    }

}
