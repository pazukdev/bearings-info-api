package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserAction;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.pazukdev.backend.util.UserActionUtil.ValueIncrease.*;

public class UserActionUtil {

    private final static Set<String> userRatedActions = new HashSet<>(Arrays.asList("create", "update", "rate"));

    @Getter
    public enum ValueIncrease {

        RATE_ITEM(1),
        UPDATE(4),
        CREATE_PART(4),
        CREATE_REPLACER(6),
        CREATE_ITEM(10),
        CREATE_MOTORCYCLE(20);

        private final Integer value;

        ValueIncrease(final Integer value) {
            this.value = value;
        }
    }

    public static void processItemAction(final String actionType,
                                         final Item item,
                                         final UserEntity user,
                                         final ItemService itemService) {
        final String itemCategory = item.getCategory();
        final String actionObject = itemCategory.equals("Motorcycle") ? itemCategory.toLowerCase() : "item";

        updateUserRating(user, actionType, actionObject);

        final UserAction action = UserActionUtil.create(user, actionType, "item", item);
        itemService.getUserActionRepository().save(action);
    }

    public static void processPartAction(final String actionType,
                                         final ChildItem part,
                                         final Item parent,
                                         final UserEntity user,
                                         final ItemService itemService) {
        updateUserRating(user, actionType, "part");

        final UserAction action = UserActionUtil.createChildItemAction(user, actionType, parent, part);
        itemService.getUserActionRepository().save(action);
    }

    public static void processReplacerAction(final String actionType,
                                             final Replacer replacer,
                                             final Item parent,
                                             final UserEntity user,
                                             final ItemService itemService) {
        updateUserRating(user, actionType, "replacer");

        final UserAction action = UserActionUtil.createReplacerAction(user, actionType, parent, replacer);
        itemService.getUserActionRepository().save(action);
    }


    public static void processRateItemAction(final Item itemToRate,
                                             final String actionType,
                                             final UserEntity user,
                                             final ItemService itemService) {
        updateUserRating(user, actionType, null);

        final UserAction action = UserActionUtil.createRateAction(itemToRate, actionType, user);
        itemService.getUserActionRepository().save(action);

    }

    private static void updateUserRating(final UserEntity user, final String actionType, final String actionObject) {
        if (!userRatedActions.contains(actionType)) {
            return;
        }
        Integer increase = getIncrease(actionType, actionObject);
        if (increase != null) {
            final Integer rating = user.getRating() + increase;
            user.setRating(rating);
        }
    }

    private static Integer getIncrease(final String actionType, final String actionObject) {
        Integer increase = null;
        if (actionType.equals("create")) {
            switch (actionObject) {
                case "motorcycle":
                    increase = CREATE_MOTORCYCLE.getValue();
                    break;
                case "item":
                    increase = CREATE_ITEM.getValue();
                    break;
                case "part":
                    increase = CREATE_PART.getValue();
                    break;
                case "replacer":
                    increase = CREATE_REPLACER.getValue();
                    break;
            }
        } else if (actionType.equals("update")) {
            increase = UPDATE.getValue();
        } else if (actionType.equals("rate")) {
            increase = RATE_ITEM.getValue();
        }
        return increase;
    }

    private static UserAction createRateAction(final Item itemToRate,
                                               final String actionType,
                                               final UserEntity user) {
        return create(user, actionType, "replacer", itemToRate);
    }

    private static UserAction createChildItemAction(final UserEntity user,
                                                   final String actionType,
                                                   final Item item,
                                                   final ChildItem childItem) {
        final String itemType = "child item";
        final Long partId = childItem.getId();

        final UserAction userAction = create(user, actionType, itemType, item);
        userAction.setItemId(partId != null ? partId.toString() : "-");
        userAction.setName(createName(actionType, itemType, childItem.getName()));
        userAction.setParentItemId(item.getId().toString());
        userAction.setItemType(itemType);

        return userAction;
    }

    private static UserAction createReplacerAction(final UserEntity user,
                                                  final String actionType,
                                                  final Item item,
                                                  final Replacer replacer) {
        final String itemType = "replacer";
        final Long replacerId = replacer.getId();

        final UserAction userAction = create(user, actionType, itemType, item);
        userAction.setItemId(replacerId != null ? replacerId.toString() : "-");
        userAction.setName(createName(actionType, itemType, replacer.getName()));
        userAction.setParentItemId(item.getId().toString());
        userAction.setItemType(itemType);

        return userAction;
    }

    private static UserAction create(final UserEntity user,
                                     final String actionType,
                                     final String itemType,
                                     final Item item) {
        final String name = createName(actionType, itemType, item.getName());

        final UserAction userAction = new UserAction();

        userAction.setName(name);
        userAction.setActionType(actionType);
        userAction.setActionDate(LocalDateTime.now().toString());

        userAction.setUserId(user.getId().toString());

        userAction.setItemId(item.getId().toString());
        userAction.setItemCategory(item.getCategory());
        userAction.setItemType(itemType);

        return userAction;
    }

    private static String createName(final String actionType, final String itemType, final String itemName) {
        return actionType + " " + itemType + " " + itemName;
    }

}
