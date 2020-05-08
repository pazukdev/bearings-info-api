package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.UserActionDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.entity.abstraction.Typeable;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.service.ItemService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;
import static com.pazukdev.backend.util.UserActionUtil.ValueIncrease.*;

/**
 * @author Siarhei Sviarkaltsau
 */
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

    public static class ValuationType {
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

    public static List<UserActionDto> getLastNewVehicles(final ItemService service) {
        final UserActionRepository repository = service.getUserActionRepo();
        final String create = ActionType.CREATE;
        final Pageable p = RepositoryUtil.getPageRequest(RepositoryUtil.LAST_TEN);
        final Page<UserAction> actions = repository.findFirst10ByActionTypeAndNote(create, "Vehicle", p);
        return getLastUserActionsReport(actions.getContent(), service);
    }

    public static List<UserActionDto> getLastNewReplacers(final ItemService service) {
        final UserActionRepository repository = service.getUserActionRepo();
        final String add = ActionType.ADD;
        final Pageable p = RepositoryUtil.getPageRequest(RepositoryUtil.LAST_TEN);
        final Page<UserAction> actions = repository.findFirst10ByActionTypeAndNote(add, "replacer", p);
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
        final Long parentId = action.getParentId();

        final UserEntity user = service.getUserService().findOne(userId);
        final Item item = service.findFirstActive(itemId);
        if (user == null || item == null) {
            return null;
        }
        if (parentId != null && parentId > 0) {
            if (service.findFirstActive(parentId) == null) {
                return null;
            }
        }

        final UserActionDto dto = new UserActionDto();
        dto.setUserId(userId);
        dto.setItemId(itemId);
        dto.setUserName(user.getName());
        dto.setItemName(item.getName());
        dto.setItemCategory(item.getCategory());
        dto.setActionType(actionType);
        dto.setDate(action.getActionDate());
        dto.setNote(action.getNote());
        final Item parent = service.findOne(action.getParentId());
        if (parent != null) {
            dto.setParentId(parent.getId());
            dto.setParentName(parent.getName());
        }
        return dto;
    }

    public static UserAction createAction(final String actionType,
                                          final String actionDetails,
                                          @Nullable final Item parent,
                                          @Nonnull final AbstractEntity entity,
                                          final UserEntity user,
                                          final boolean specifyParentInMessage) {

        if (user == null) {
            return null;
        }

        String message =  entity.toString();
        if (entity instanceof NestedItem) {
            message += " item=(" + ((NestedItem) entity).getItem() + ")";
        }
        if (!actionType.equals(ActionType.UPDATE)) {
            message += ": " + actionType.toLowerCase() + "(e)d";
            if (actionType.equals(ActionType.DELETE) && specifyParentInMessage && parent != null) {
                String preposition = "from";
                message += " " + preposition + " " + parent;
            }
        }
        if (!isEmpty(actionDetails)) {
            if (entity instanceof Item) {
                message = actionDetails;
            } else {
                message += ": " + actionDetails;
            }
        }

        final String valuationType;
        if (entity instanceof Typeable) {
            valuationType = ((Typeable) entity).getValuationType();
        } else {
            valuationType = ValuationType.ITEM;
        }

        Long parentId = null;
        if (parent != null) {
            parentId = parent.getId();
        }

        String note = "-";
        Long itemId = entity.getId();
        if (entity instanceof Item) {
            note = ((Item) entity).getCategory().toLowerCase();
        } else if (entity instanceof NestedItem) {
            final NestedItem nestedItem = (NestedItem) entity;
            note = nestedItem.getType();
            itemId = nestedItem.getItem().getId();
        } else if (entity instanceof Link) {
            note = "link";
        }

        final UserAction action = new UserAction();
        action.setActionType(actionType);
        action.setActionDate(DateTimeUtil.now());
        action.setUserId(user.getId());
        action.setParentId(parentId != null ? parentId : 0L);
        action.setItemId(itemId != null ? itemId : 0L);
        action.setMessage(message);
        action.setNote(note);

        if (!(entity instanceof UserEntity)) {
            updateUserRating(user, actionType, valuationType);
        }

        return action;
    }

    public static List<UserAction> createActions(final Item parent,
                                                 final Set<NestedItem> oldChildren,
                                                 final Set<NestedItem> newChildren,
                                                 final UserEntity user,
                                                 final ItemService service) {

        final List<UserAction> actions = new ArrayList<>();

        for (final NestedItem child : newChildren) {
            if (child.getId() == null) {
                actions.add(createAction(ActionType.ADD, "", parent, child, user, false));
            }
        }

        String actionDetails = "";
        final List<NestedItem> toSave = new ArrayList<>();
        for (final NestedItem oldChild : oldChildren) {
            for (final NestedItem newChild : newChildren) {
                if (newChild.getName().equals(oldChild.getName())) {
                    toSave.add(oldChild);
                    final String newComment = newChild.getComment();
                    final String oldComment = oldChild.getComment();
                    final String newQuantity = newChild.getQuantity();
                    final String oldQuantity = oldChild.getQuantity();
                    final boolean commentChanged = !Objects.equals(newComment, oldComment);
                    final boolean quantityChanged = !Objects.equals(newQuantity, oldQuantity);
                    if (commentChanged) {
                        actionDetails += "new comment: " + newComment;
                    }
                    if (quantityChanged) {
                        actionDetails += "new quantity: " + newQuantity;
                    }
                    if (commentChanged || quantityChanged) {
                        actions.add(createAction(ActionType.UPDATE, actionDetails, parent, oldChild, user, false));
                    }
                }
            }
        }

        oldChildren.removeAll(toSave);

        for (final NestedItem orphan : oldChildren) {
            service.getNestedItemRepo().deleteById(orphan.getId());
            actions.add(createAction(ActionType.DELETE, "", parent, orphan, user, false));
        }

        return actions;
    }

    public static void createActions(final HeaderTable header,
                                     final Item oldItem,
                                     final String newStatus,
                                     final List<UserAction> actions,
                                     final UserEntity user) {

        final List<HeaderTableRow> rows = new ArrayList<>(header.getRows());
//        rows.add(HeaderTableRow.create("Status", newStatus));

        final Map<String, String> map = ItemUtil.toMap(oldItem.getDescription());
        map.put("Name", oldItem.getName());
        map.put("Category", oldItem.getCategory());
//        map.put("Status", oldItem.getStatus());

        for (final HeaderTableRow row : rows) {
            final String param = row.getParameter();
            final String value = row.getValue();
            final boolean newRow = row.getId() == null;
            if (newRow) {
                final String actionDetails = "new param=" + param + " value=" + value + " added";
                actions.add(createAction(ActionType.ADD, actionDetails, null, oldItem, user, false));
            } else {
                String actionDetails = null;
                final String oldValue = map.get(param);
                if (oldValue == null) {
                    actionDetails = "param param=" + param + " value=" + value
                            + ": new param=" + param;
                } else if (!oldValue.equals(value)) {
                    actionDetails = "param param=" + param + " value=" + oldValue
                            + ": new value=" + value;
                }
                if (actionDetails != null) {
                    actions.add(createAction(ActionType.UPDATE, actionDetails, null, oldItem, user, false));
                }
            }
        }

        for (final Map.Entry<String, String> entry : map.entrySet()) {
            boolean deleted = true;
            for (final HeaderTableRow row : header.getRows()) {
                if (row.getName().equals(entry.getKey())) {
                    deleted = false;
                    break;
                }
            }
            if (deleted) {
                final String actionDetails = "param " + entry.getKey() + " removed";
                actions.add(createAction(ActionType.DELETE, actionDetails, null, oldItem, user, false));
            }
        }
    }

    public static void updateUserRating(final UserEntity user,
                                        final String actionType,
                                        final String valuationType) {
        if (!userRatedActions.contains(actionType)) {
            return;
        }
        Integer increase = getIncrease(actionType, valuationType);
        if (increase != null) {
            final Integer rating = user.getRating() + increase;
            user.setRating(rating);
        }
    }

    private static Integer getIncrease(final String actionType, final String valuationType) {

        Integer increase = null;
        if (actionType.equals(ActionType.CREATE)) {
            switch (valuationType) {
                case ValuationType.VEHICLE:
                    increase = CREATE_VEHICLE.getValue();
                    break;
                case ValuationType.ITEM:
                    increase = CREATE_ITEM.getValue();
                    break;
            }
        } else if (actionType.equals(ActionType.ADD)) {
            switch (valuationType) {
                case ValuationType.PART:
                    increase = ADD_PART.getValue();
                    break;
                case ValuationType.REPLACER:
                    increase = ADD_REPLACER.getValue();
                    break;
                case ValuationType.LINK:
                    increase = ADD_OR_UPDATE_LINK.getValue();
                    break;
            }
        } else if (actionType.equals(ActionType.UPDATE)) {
            if (valuationType.equals(ValuationType.LINK)) {
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

    private static String createChildName(final String actionType,
                                          final Item child,
                                          final Item parent,
                                          final String itemType) {
        final String toOrFrom = actionType.equals("add") ? "to" : "from";
        return actionType + " " + child.getName() + " " + toOrFrom + " " + parent.getName() + " as " + itemType;
    }

}
