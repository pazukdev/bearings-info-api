package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.RateReplacer;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;

import java.util.Objects;

import static com.pazukdev.backend.util.UserActionUtil.ActionType;

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
                                            final UserEntity user,
                                            final ItemService service) {

        final Item item = service.findOne(rate.getItemId());
        final RateAction rateAction = RateAction.valueOf(rate.getAction().toUpperCase());

        if (rateAction == RateAction.UP) {
            item.getLikedUsers().add(user);
            item.getDislikedUsers().remove(user);
        } else if (rateAction == RateAction.DOWN) {
            item.getDislikedUsers().add(user);
            item.getLikedUsers().remove(user);
        } else {
            item.getLikedUsers().remove(user);
            item.getDislikedUsers().remove(user);
        }

        service.getRepository().save(item);

        for (final NestedItemDto replacer : rate.getReplacers()) {
            if (Objects.equals(replacer.getItemId(), item.getId())) {
                replacer.setLikedUsers(NestedItemUtil.getLikedUserDtos(item.getLikedUsers()));
                replacer.setDislikedUsers(NestedItemUtil.getLikedUserDtos(item.getDislikedUsers()));
            }
        }

        final String actionType = rateAction == RateAction.CANCEL ? ActionType.CANCEL_RATE : ActionType.RATE;
        LoggerUtil.warn(
                UserActionUtil.createAction(actionType, "", null, item, user, false),
                service.getUserActionRepo(),
                item,
                user,
                service.getEmailSenderService());

        rate.setNewUserRating(user.getRating());
        return rate;
    }

}
