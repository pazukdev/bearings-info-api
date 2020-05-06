package com.pazukdev.backend.converter;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.ChildItemUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NestedItemConverter {

    public static Set<NestedItem> convert(final List<NestedItemDto> dtos,
                                          final Long parentId,
                                          final ItemService itemService,
                                          final UserEntity user) {
        final String parentName = ChildItemUtil.getParentName(parentId, itemService);

        final Set<NestedItem> items = new HashSet<>();
        for (final NestedItemDto dto : dtos) {
            final Item partItem = itemService.findOne(dto.getItemId());

            final NestedItem item = new NestedItem();
            item.setId(dto.getId());
            item.setName(ChildItemUtil.getName(parentName, partItem.getName()));
            item.setItem(partItem);
            item.setComment(dto.getComment());
            item.setQuantity(dto.getSecondComment());
            item.setStatus(dto.getStatus());
            item.setType(dto.getType());
            item.setCreatorId(user.getId());

            items.add(item);
        }

        return items;
    }

}
