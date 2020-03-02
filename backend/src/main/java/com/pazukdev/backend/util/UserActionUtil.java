package com.pazukdev.backend.util;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.dto.UserActionDto;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static com.pazukdev.backend.util.CategoryUtil.Category;
import static com.pazukdev.backend.util.UserActionUtil.ValueIncrease.*;

public class UserActionUtil {

    public static class ActionType {
        public static final String ADD = "add";
        public static final String CANCEL_RATE = "cancel rate";
        public static final String CREATE = "create";
        public static final String DELETE = "delete";
        public static final String RATE = "rate";
        public static final String UPDATE = "update";
        public static final String UPLOAD_DICTIONARY = "upload dictionary";
    }

    public static class ItemType {
        public static final String VEHICLE = "vehicle";
        public static final String ITEM = "item";
        public static final String PART = "part";
        public static final String REPLACER = "replacer";
        public static final String LINK = "link";
    }

    private final static Set<String> userRatedActions = new HashSet<>(Arrays
            .asList(ActionType.CREATE, ActionType.ADD, ActionType.UPDATE, ActionType.RATE));

    @Getter
    public enum ValueIncrease {

        RATE_ITEM(1),
        UPLOAD_DICTIONARY(2),
        ADD_OR_UPDATE_LINK(4),
        UPDATE(4),
        ADD_PART(4),
        ADD_REPLACER(6),
        CREATE_ITEM(10),
        CREATE_VEHICLE(20);

        private final Integer value;

        ValueIncrease(final Integer value) {
            this.value = value;
        }
    }

    private static Pageable getPageRequest(int size) {
        return PageRequest.of(0, size, Sort.Direction.DESC, "id");
    }

    public static List<UserActionDto> getLastNewVehicles(final ItemService service) {
        final UserActionRepository repository = service.getUserActionRepository();
        final String create = ActionType.CREATE;
        final Pageable p = getPageRequest(10);
        final Page<UserAction> actions = repository.findFirst10ByActionTypeAndItemCategory(create, "Vehicle", p);
        return getLastUserActionsReport(actions.getContent(), service);
    }

    public static List<UserActionDto> getLastNewReplacers(final ItemService service) {
        final UserActionRepository repository = service.getUserActionRepository();
        final String add = ActionType.ADD;
        final Pageable p = getPageRequest(10);
        final Page<UserAction> actions = repository.findFirst10ByActionTypeAndItemType(add, "replacer", p);
        return getLastUserActionsReport(actions.getContent(), service);
    }

    public static List<UserActionDto> getLastUserActionsReport(final List<UserAction> actions,
                                                              final ItemService service) {
        final List<UserActionDto> lastUsersActions = new ArrayList<>();
        for (final UserAction action : actions) {
            final UserActionDto actionDto = toDto(action, service);
            if (actionDto != null) {
                lastUsersActions.add(actionDto);
            }
        }
        return lastUsersActions;
    }

    public static UserActionDto toDto(final UserAction action, final ItemService service) {
        String actionType = "-";
        if (action.getActionType().equals(ActionType.CREATE)) {
            actionType = "created";
        } else if (action.getActionType().equals(ActionType.ADD)) {
            actionType = "added";
        } else {
            return null;
        }

        final Long userId = action.getUserId();
        final Long itemId = action.getItemId();

        final UserEntity user = service.getUserService().findOne(userId);
        final Item item = service.findOne(itemId);
        if (user == null || item == null || !item.getStatus().equals(Status.ACTIVE)) {
            return null;
        }

        final UserActionDto dto = new UserActionDto();
        dto.setUserId(userId);
        dto.setItemId(itemId);
        dto.setUserName(user.getName());
        dto.setItemName(item.getName());
        dto.setActionType(actionType);
        dto.setItemType(action.getItemType());
        dto.setDate(action.getActionDate());
        dto.setItemCategory(action.getItemCategory());

        final Long parentId = action.getParentItemId();
        if (parentId != null) {
            final Item parent = service.findOne(parentId);
            if (parent != null) {
                dto.setParentId(parent.getId());
                dto.setParentName(parent.getName());
            }
        }
        return dto;
    }

    public static void processLinkAction(final String actionType,
                                         final String linkType,
                                         final Item item,
                                         final UserEntity user,
                                         final ItemService itemService) {
        final String itemType = ItemType.LINK;

        updateUserRating(user, actionType, itemType);

        final UserAction action = create(user, actionType, itemType, item);
        action.setName(action.getName().replace(itemType, linkType + " " + itemType + " for"));
        itemService.getUserActionRepository().save(action);
    }

    public static void processItemAction(final String actionType,
                                         final Item item,
                                         final UserEntity user,
                                         final ItemService itemService) {
        final String category = item.getCategory();
        final String itemType = category.equals(Category.VEHICLE) ? category.toLowerCase() : ItemType.ITEM;

        updateUserRating(user, actionType, itemType);

        final UserAction action = create(user, actionType, itemType, item);
        itemService.getUserActionRepository().save(action);
    }

    public static void processPartAction(final String actionType,
                                         final ChildItem part,
                                         final Item parent,
                                         final UserEntity user,
                                         final ItemService itemService) {
        updateUserRating(user, actionType, ItemType.PART);

        final UserAction action = createChildItemAction(user, actionType, parent, part);
        itemService.getUserActionRepository().save(action);
    }

    public static void processReplacerAction(final String actionType,
                                             final Replacer replacer,
                                             final Item parent,
                                             final UserEntity user,
                                             final ItemService itemService) {
        updateUserRating(user, actionType, ItemType.REPLACER);

        final UserAction action = createReplacerAction(user, actionType, parent, replacer);
        itemService.getUserActionRepository().save(action);
    }


    public static int processRateItemAction(final Item itemToRate,
                                             final String actionType,
                                             final UserEntity user,
                                             final ItemService itemService) {
        final int newUserRating = updateUserRating(user, actionType, null);

        final UserAction action = createRateAction(itemToRate, actionType, user);
        itemService.getUserActionRepository().save(action);
        return newUserRating;
    }

    public static void processUploadDictionaryAction(final String actionType,
                                                     final String changed,
                                                     final UserEntity user,
                                                     final UserActionRepository repository) {
        updateUserRating(user, actionType, null);

        final UserAction userAction = new UserAction();

        userAction.setName(actionType + changed);
        userAction.setActionType(actionType);
        userAction.setActionDate(LocalDateTime.now().toString());
        userAction.setUserId(user.getId());
        userAction.setItemId(0L);

        repository.save(userAction);
    }

    private static int updateUserRating(final UserEntity user, final String actionType, final String itemType) {
        if (!userRatedActions.contains(actionType)) {
            return user.getRating();
        }
        Integer increase = getIncrease(actionType, itemType);
        if (increase != null) {
            final Integer rating = user.getRating() + increase;
            user.setRating(rating);
        }
        return user.getRating();
    }

    private static Integer getIncrease(final String actionType, final String itemType) {
        Integer increase = null;
        if (actionType.equals(ActionType.CREATE)) {
            switch (itemType) {
                case ItemType.VEHICLE:
                    increase = CREATE_VEHICLE.getValue();
                    break;
                case ItemType.ITEM:
                    increase = CREATE_ITEM.getValue();
                    break;
            }
        } else if (actionType.equals(ActionType.ADD)) {
            switch (itemType) {
                case ItemType.PART:
                    increase = ADD_PART.getValue();
                    break;
                case ItemType.REPLACER:
                    increase = ADD_REPLACER.getValue();
                    break;
                case ItemType.LINK:
                    increase = ADD_OR_UPDATE_LINK.getValue();
                    break;
            }
        } else if (actionType.equals(ActionType.UPDATE)) {
            if (itemType.equals(ItemType.LINK)) {
                increase = ADD_OR_UPDATE_LINK.getValue();
            } else {
                increase = UPDATE.getValue();
            }
        } else if (actionType.equals(ActionType.RATE)) {
            increase = RATE_ITEM.getValue();
        } else if (actionType.equals(ActionType.UPLOAD_DICTIONARY)) {
            increase = UPLOAD_DICTIONARY.getValue();
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
        final String itemType = "part";

        final UserAction userAction = create(user, actionType, itemType, item);
        userAction.setItemId(childItem.getItem().getId());
        userAction.setName(createChildName(actionType, childItem.getItem(), item, itemType));
        userAction.setParentItemId(item.getId());
        userAction.setItemType(itemType);
        userAction.setItemCategory(childItem.getItem().getCategory());

        return userAction;
    }

    private static UserAction createReplacerAction(final UserEntity user,
                                                  final String actionType,
                                                  final Item item,
                                                  final Replacer replacer) {
        final String itemType = "replacer";

        final UserAction userAction = create(user, actionType, itemType, item);
        userAction.setItemId(replacer.getItem().getId());
        userAction.setName(createChildName(actionType, replacer.getItem(), item, itemType));
        userAction.setParentItemId(item.getId());
        userAction.setItemType(itemType);

        return userAction;
    }

    private static String createChildName(final String actionType,
                                          final Item child,
                                          final Item parent,
                                          final String itemType) {
        final String toOrFrom = actionType.equals("add") ? "to" : "from";
        return actionType + " " + child.getName() + " " + toOrFrom + " " + parent.getName() + " as " + itemType;
    }

    private static UserAction create(final UserEntity user,
                                     final String actionType,
                                     final String itemType,
                                     final Item item) {
        final String name = actionType + " " + itemType + " " + item.getName();

        final UserAction userAction = new UserAction();
        userAction.setName(name);
        userAction.setActionType(actionType);
        userAction.setActionDate(DateTimeUtil.now());
        userAction.setUserId(user.getId());
        userAction.setItemId(item.getId());
        userAction.setItemCategory(item.getCategory());
        userAction.setItemType(itemType);
        return userAction;
    }

}
