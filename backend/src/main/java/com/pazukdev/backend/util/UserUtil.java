package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.LikeListDto;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;

public class UserUtil {

    public static LikeListDto createLikeListDto(final UserEntity user) {
        final LikeListDto dto = new LikeListDto();
        dto.getLikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getLikedItems()));
        dto.getDislikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getDislikedItems()));
        return dto;
    }

    public static UserDto getCreatorData(final Item item, final UserService service) {
        final UserEntity user = service.findOne(item.getCreatorId());
        if (user != null) {
            final UserDto creatorData = new UserDto();
            creatorData.setId(user.getId());
            creatorData.setName(user.getName());
            creatorData.setStatus(user.getStatus());
            return creatorData;
        }
        return null;
    }

}
