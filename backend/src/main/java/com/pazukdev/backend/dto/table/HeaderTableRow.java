package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//    private String message = "-";
    private boolean deletable = true;

    public static HeaderTableRow create(final String parameter, final String value, final String itemCategory) {
        final HeaderTableRow headerTableRow = new HeaderTableRow();
        headerTableRow.setName(parameter);
        headerTableRow.setParameter(parameter);
        headerTableRow.setValue(value);
        headerTableRow.setDeletable(isDeletable(parameter, itemCategory));
        return headerTableRow;
    }

    private static boolean isDeletable(final String parameter, final String itemCategory) {
        if (parameter.toLowerCase().equals("name")) {
            return false;
        }

        if (itemCategory.toLowerCase().equals("motorcycle")) {
            final List<String> fixedParams = new ArrayList<>(Arrays.asList("production", "manufacturer"));
            return !fixedParams.contains(parameter.toLowerCase());
        }

        return true;
    }

}
