package com.pazukdev.backend.converter;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ChildItemUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.util.SpecificStringUtil.capitalize;

public class NestedItemConverter {

    public static NestedItem convert(final NestedItemDto dto, final Item child) {
        final NestedItem item = new NestedItem();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setItem(child);
        item.setComment(dto.getComment());
        item.setQuantity(dto.getSecondComment());
        item.setStatus(dto.getStatus());
        item.setType(dto.getType());
        item.setCreatorId(dto.getCreatorId());

        return item;
    }

    public static NestedItem convert(final NestedItemDto dto,
                                     final String parentName,
                                     final ItemService service) {
        final Item child = service.findOne(dto.getItemId());

        final NestedItem item = convert(dto, child);
        item.setName(ChildItemUtil.getName(parentName, child.getName()));

        return item;
    }

    public static NestedItemDto convert(final NestedItem item,
                                        final UserService service,
                                        final boolean addLocation) {
        final Item child = item.getItem();

        final NestedItem.Type type = NestedItem.Type.valueOf(item.getType().toUpperCase());
        final NestedItemDto childItemDto = NestedItemDto.create(child, type, service);
        childItemDto.setId(item.getId());
        childItemDto.setName(item.getName());
        childItemDto.setSecondComment(item.getQuantity());
        if (addLocation) {
            childItemDto.setComment(item.getComment());
        }

        return childItemDto;
    }

    public static Set<NestedItem> convert(final List<NestedItemDto> dtos,
                                          final Long parentId,
                                          final ItemService itemService,
                                          final UserEntity user) {
        final String parentName = ChildItemUtil.getParentName(parentId, itemService);

        final Set<NestedItem> items = new HashSet<>();
        dtos.forEach(dto -> {
            dto.setCreatorId(user.getId());
            items.add(convert(dto, parentName, itemService));
        });
        return items;
    }

    public static NestedItemDto convert(final UserEntity user) {
        final String role = capitalize(user.getRole().name());

        final NestedItemDto userDto = NestedItemDto.create();
        userDto.setItemId(user.getId());
        userDto.setItemName(user.getName());
        userDto.setButtonText(user.getName());
        userDto.setRating(user.getRating());
        userDto.setItemCategory(user.getStatus());
        userDto.setComment(role);
        userDto.setSecondComment(user.getRating().toString());
        userDto.setStatus(user.getStatus());

        return userDto;
    }

}
