package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.LikeListDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.LikeList;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;

import java.util.HashSet;
import java.util.Set;

public class UserUtil {

    public static Set<Long> collectWishListItemsIds(final UserEntity currentUser) {
        Set<Long> ids = new HashSet<>();
        for (final ChildItem item : currentUser.getWishList().getItems()) {
            ids.add(item.getItem().getId());
        }
        return ids;
    }

    public static Set<Long> collectRatedItemIds(final UserEntity user) {
        final LikeList likeList = user.getLikeList();
        final Set<Long> ratedItemIds = new HashSet<>();
        ratedItemIds.addAll(ItemUtil.collectIds(likeList.getLikedItems()));
        ratedItemIds.addAll(ItemUtil.collectIds(likeList.getDislikedItems()));
        return ratedItemIds;
    }

    public static Set<Long> collectLikedItemIds(final UserEntity user) {
        return ItemUtil.collectIds(user.getLikeList().getLikedItems());
    }

    public static Set<Long> collectDislikedItemIds(final UserEntity user) {
        return ItemUtil.collectIds(user.getLikeList().getDislikedItems());
    }

    public static LikeListDto createLikeListDto(final UserEntity user) {
        final LikeListDto dto = new LikeListDto();
        dto.getLikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getLikedItems()));
        dto.getDislikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getDislikedItems()));
        return dto;
    }

    public static String getCreatorName(final Item item, final UserService userService) {
        final Long userId = item.getCreatorId();
        final boolean userDeleted = !userService.getRepository().existsById(userId);
        if (userDeleted) {
            return "deleted user";
        }
        return userService.getOne(item.getCreatorId()).getName();
    }

}
