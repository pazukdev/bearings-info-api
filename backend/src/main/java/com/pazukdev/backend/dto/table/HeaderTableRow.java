package com.pazukdev.backend.dto.table;

import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.CategoryUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.pazukdev.backend.util.CategoryUtil.getCategory;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderTableRow extends AbstractDto {

    private String parameter = "-";
    private String value = "-";
    private List<Long> ids = new ArrayList<>();
    private int weight = 0;

    public static HeaderTableRow create(final String param, final String value, final ItemService service) {
        final List<Long> ids= new ArrayList<>();
        for (final String subValue : value.split("; ")) {
            final Item item = service.findFirstByCategoryAndName(getCategory(param), subValue);
            if (item != null) {
                ids.add(item.getId());
            } else {
                ids.add(null);
            }
        }

        final HeaderTableRow row = new HeaderTableRow();
        row.setName(param);
        row.setParameter(param);
        row.setValue(value);
        row.setIds(ids);
        row.setWeight(CategoryUtil.getWeight(param));
        return row;
    }

}
