package com.pazukdev.backend.dto;

import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.pazukdev.backend.util.CategoryUtil.Category;
import static com.pazukdev.backend.util.CategoryUtil.isPart;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PossibleNestedItemsDto {

    private final List<NestedItemDto> parts = new ArrayList<>();
    private final List<NestedItemDto> replacers = new ArrayList<>();

    public static PossibleNestedItemsDto create(final List<Item> allItems,
                                                final Item parent,
                                                final List<String> infoCategories,
                                                final ItemService service) {

        final UserService userService = service.getUserService();
        final PossibleNestedItemsDto possibleNestedItemsDto = new PossibleNestedItemsDto();

        for (final Item item : allItems) {
            final String category = item.getCategory();
            if (category.equals(Category.VEHICLE)) {
                continue;
            }

            boolean addPart = isPart(category, infoCategories);
            boolean addReplacer = category.equals(parent.getCategory())
                    || (parent.getCategory().equals(Category.RUBBER_PART) && category.equals(Category.BEARING));

            NestedItemDto dto = null;
            if (addPart) {
                dto = NestedItemDto.createPart(item, userService);
                possibleNestedItemsDto.getParts().add(dto);
            }
            if (addReplacer) {
                if (dto == null) {
                    dto = NestedItemDto.createReplacer(item, userService);
                }
                possibleNestedItemsDto.getReplacers().add(dto);
            }
        }

        return possibleNestedItemsDto;
    }

}
