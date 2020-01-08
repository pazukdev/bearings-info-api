package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.RateReplacer;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.LikeList;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;

import java.util.Objects;

public class RateUtil {

    @Getter
    public enum RateAction {

        UP("up"),
        DOWN("down"),
        CANCEL("cancel");

        private final String value;

        RateAction(final String value) {
            this.value = value;
        }
    }

    public static RateReplacer rateReplacer(final RateReplacer rate,
                                            final UserEntity currentUser,
                                            final ItemService itemService) {

        final Long itemId = rate.getItemId();
        final Item itemToRate = itemService.getOne(rate.getItemId());
        final RateAction rateAction = RateAction.valueOf(rate.getAction().toUpperCase());
        final LikeList likeList = currentUser.getLikeList();

        if (rateAction == RateAction.CANCEL) {
            if (likeList.getLikedItems().contains(itemToRate)) {
                likeList.getLikedItems().remove(itemToRate);
                itemToRate.setRating(itemToRate.getRating() - 1);
            } else {
                likeList.getDislikedItems().remove(itemToRate);
                itemToRate.setRating(itemToRate.getRating() + 1);
            }
        } else {
            if (rateAction == RateAction.UP) {
                itemToRate.setRating(itemToRate.getRating() + 1);
                itemService.update(itemToRate);
                currentUser.getLikeList().getLikedItems().add(itemToRate);
            } else if (rateAction == RateAction.DOWN) {
                itemToRate.setRating(itemToRate.getRating() - 1);
                itemService.update(itemToRate);
                currentUser.getLikeList().getDislikedItems().add(itemToRate);
            }
        }

        final String actionType = rateAction == RateAction.CANCEL ? "cancel rate" : "rate";
        UserActionUtil.processRateItemAction(itemToRate, actionType, currentUser, itemService);

        for (final NestedItemDto replacer : rate.getReplacers()) {
            if (Objects.equals(replacer.getItemId(), itemId)) {
                replacer.setRating(itemToRate.getRating());
            }
        }

        rate.setLikeList(UserUtil.createLikeListDto(currentUser));
        return rate;
    }

}
