package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.dto.factory.NestedItemDtoFactory.createBasicNestedItemDto;
import static com.pazukdev.backend.util.CategoryUtil.Category;
import static com.pazukdev.backend.util.CategoryUtil.isPart;

public class NestedItemUtil {

    public static String createName(final String parentItemName, final String nestedItemName) {
        return parentItemName + " - " + nestedItemName;
    }

    public static void addPossiblePartsAndReplacers(final ItemView view,
                                                    final List<Item> allItems,
                                                    final Item parent,
                                                    final List<String> infoCategories,
                                                    final ItemService itemService) {
        final UserService userService = itemService.getUserService();

        for (final Item item : allItems) {
            final String category = item.getCategory();
            if (category.equals(Category.VEHICLE)) {
                continue;
            }

            boolean addPart = isPart(category, infoCategories) && !category.equals(parent.getCategory());
            boolean addReplacer = category.equals(parent.getCategory())
                    || (parent.getCategory().equals("Rubber part") && category.equals("Bearing"));

            NestedItemDto dto = null;
            if (addPart) {
                dto = createBasicNestedItemDto(item, userService);
                dto.setType(NestedItem.Type.PART.name().toLowerCase());
                view.getPossibleParts().add(dto);
            }
            if (addReplacer) {
                if (dto == null) {
                    dto = createBasicNestedItemDto(item, userService);
                    dto.setType(NestedItem.Type.REPLACER.name().toLowerCase());
                }
                view.getPossibleReplacers().add(dto);
            }
        }
    }

    public static Set<NestedItemDto> getLikedUserDtos(final Set<UserEntity> users) {
        final Set<NestedItemDto> userDtos = new HashSet<>();
        for (final UserEntity user : users) {
            final NestedItemDto userDto = new NestedItemDto();
            userDto.setItemId(user.getId());
            userDto.setItemName(user.getName());
            userDto.setComment(user.getCountry());
            userDtos.add(userDto);
        }
        return userDtos;
    }

}
