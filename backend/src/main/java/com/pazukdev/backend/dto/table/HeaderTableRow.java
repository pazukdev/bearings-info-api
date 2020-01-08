package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.config.ContextData;
import com.pazukdev.backend.dto.AbstractDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderTableRow extends AbstractDto {

    private String parameter = "-";
    private String value = "-";
    private String itemId = "-";
    private boolean deletable;
    private int weight = 0;

    public static HeaderTableRow create(final String parameter, final String value) {
        final HeaderTableRow headerTableRow = new HeaderTableRow();
        headerTableRow.setName(parameter);
        headerTableRow.setParameter(parameter);
        headerTableRow.setValue(value);
        headerTableRow.setDeletable(!ContextData.isFixed(parameter));
        headerTableRow.setWeight(ContextData.getWeight(parameter));
        return headerTableRow;
    }

}
