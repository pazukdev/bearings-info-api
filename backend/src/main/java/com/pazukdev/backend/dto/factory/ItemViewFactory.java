package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.TableDto;
import com.pazukdev.backend.dto.table.TableViewDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.entity.WishList;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.DateUtil;
import com.pazukdev.backend.util.ImageUtil;
import com.pazukdev.backend.util.ItemUtil;
import com.pazukdev.backend.util.NestedItemUtil;
import com.pazukdev.backend.util.RateUtil;
import com.pazukdev.backend.util.UserActionUtil;
import com.pazukdev.backend.util.UserUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pazukdev.backend.util.TableUtil.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public class ItemViewFactory {

    private final ItemService itemService;

    @Getter
    private enum SpecialItemId {

        ITEMS_MANAGEMENT_VIEW(-1),
        MOTORCYCLE_CATALOGUE_VIEW(-2),
        WISH_LIST_VIEW(-3),
        USER_LIST_VIEW(-4);

        private final int itemId;

        SpecialItemId(final int itemId) {
            this.itemId = itemId;
        }
    }

    public ItemView createItemView(final Long itemId, final String userName) {
        final UserEntity currentUser = itemService.getUserService().findByName(userName);
        final WishList wishList = currentUser.getWishList();

        final ItemView itemView = new ItemView();
        itemView.setItemId(itemId);
        itemView.setWishListIds(UserUtil.collectWishListItemsIds(currentUser));
        itemView.setUserData(NestedItemDtoFactory.createUser(currentUser));

        if (itemId == SpecialItemId.WISH_LIST_VIEW.getItemId()) {
            return createWishListView(itemView, wishList);
        }

        if (itemId == SpecialItemId.MOTORCYCLE_CATALOGUE_VIEW.getItemId()) {
            return createMotorcycleCatalogueView(itemView);
        }

        if (itemId == SpecialItemId.ITEMS_MANAGEMENT_VIEW.getItemId()) {
            return createItemsManagementView(itemView);
        }

        if (itemId == SpecialItemId.USER_LIST_VIEW.getItemId()) {
            return createUsersListView(itemView);
        }

        return createOrdinaryItemView(itemView, itemId, currentUser);
    }

    public ItemView createNewItemView(final String category, final String name, final String userName) {
        final UserEntity creator = itemService.getUserService().findByName(userName);

        final Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setCreatorId(creator.getId());
        item.setUserActionDate(DateUtil.now());
        item.setDescription(createEmptyDescription(category));
        itemService.update(item);

        UserActionUtil.processItemAction("create", item, creator, itemService);

        final ItemView itemView = createItemView(item.getId(), userName);
        itemView.setNewItem(true);
        return itemView;
    }

    public ItemView updateItemView(final Long itemId,
                                   final String userName,
                                   final ItemView itemView) {
        final UserEntity user = itemService.getUserService().findByName(userName);
        final boolean removeItem = itemId == SpecialItemId.ITEMS_MANAGEMENT_VIEW.getItemId();
        final boolean removeItemFromWishList = itemId == SpecialItemId.WISH_LIST_VIEW.getItemId();
        final boolean removeUser = itemId == SpecialItemId.USER_LIST_VIEW.getItemId();

        if (removeItem) {
            return removeItem(itemView, user, itemService.getUserService());
        }
        if (removeItemFromWishList) {
            return removeItemFromWishList(itemView, user);
        }
        if (removeUser) {
            return removeUsers(itemView);
        }
        return updateItem(itemId, itemView, user);
    }

    private ItemView createOrdinaryItemView(final ItemView itemView, final Long itemId, final UserEntity currentUser) {
        final Item item = itemService.getOne(itemId);
        final List<Item> allItems = itemService.findAll();
        final List<Item> sameCategoryItems = itemService.find(item.getCategory(), allItems);
        final String tableName = "Parts";

        itemView.setSearchEnabled(true);
        itemView.setCategory(item.getCategory());
        itemView.setImage(ImageUtil.getImage(item));
        itemView.setHeader(createHeader(item, itemService));
        itemView.setItems(createTableView(new ArrayList<>(item.getChildItems())));
        itemView.setPartsTable(createPartsTable(item, tableName, itemService));
        itemView.setReplacersTable(createReplacersTable(item, itemService.getUserService()));
        itemView.getPossibleParts().addAll(NestedItemUtil.createPossibleParts(allItems, itemService.getUserService()));
        itemView.getReplacers().addAll(NestedItemUtil.createReplacerDtos(sameCategoryItems, itemService.getUserService()));
        itemView.setCreatorId(item.getCreatorId());
        itemView.setCreatorName(UserUtil.getCreatorName(item, itemService.getUserService()));
        itemView.getRatedItems().addAll(UserUtil.collectRatedItemIds(currentUser));
        return itemView;
    }

    private ItemView createMotorcycleCatalogueView(final ItemView itemView) {
        final List<Item> motorcycles = itemService.find("Motorcycle");
        final String tableName = "Motorcycle catalogue";
        final String countParameterName = "Model";

        itemView.setImage("common/ic_launcher.png");

        return createItemsView(
                itemView,
                motorcycles.size(),
                tableName,
                countParameterName,
                motorcyclesTable(motorcycles, countParameterName, itemService.getUserService()));
    }

    private ItemView createUsersListView(final ItemView itemView) {
        final List<UserEntity> users = itemService.getUserService().findAll();
        final String tableName = "Users";
        final String countParameterName = "User";

        return createItemsView(
                itemView,
                users.size(),
                tableName,
                countParameterName,
                usersTable(users, tableName));
    }

    private ItemView createItemsManagementView(final ItemView itemView) {
        final List<Item> allItems = itemService.findAll();
        final String tableName = "Items management";
        final String countParameterName = "Items";

        return createItemsView(
                itemView,
                allItems.size(),
                tableName,
                countParameterName,
                specialItemsTable(allItems, countParameterName, itemService));
    }

    private ItemView createWishListView(final ItemView itemView, final WishList wishList) {
        final List<Item> allItems = new ArrayList<>(wishList.getItems());
        final String tableName = "Your Wishlist";
        final String countParameterName = "Items";

        return createItemsView(
                itemView,
                allItems.size(),
                tableName,
                countParameterName,
                specialItemsTable(allItems, countParameterName, itemService));
    }

    private ItemView createItemsView(final ItemView itemView,
                                     final Integer size,
                                     final String tableName,
                                     final String parameter,
                                     final PartsTable table) {

        List<String[]> list = new ArrayList<>();
        list.add(new String[]{parameter, String.valueOf(size)});

        itemView.setHeader(new TableDto(tableName, listToMatrix(list)));
        final int noMatterWhatNumber = 123;
        final List<TableDto> tables = new ArrayList<>(Collections.singletonList(stubTable()));
        itemView.setItems(new TableViewDto(noMatterWhatNumber, tables));
        itemView.setPartsTable(table);
        itemView.setReplacersTable(stubReplacersTable());
        itemView.setCategories(itemService.findAllCategories());
        return itemView;
    }

    private ItemView updateItem(final Long itemId, final ItemView itemView, final UserEntity currentUser) {
        final Item item = itemService.getOne(itemId);

        if (itemView.getRate() != null) {
            RateUtil.processRateItemAction(itemView, currentUser, itemService);
            itemView.setRate(null);
            return createItemView(itemId, currentUser.getName());
        }

        final Map<String, String> headerMatrixMap = createHeaderMatrixMap(itemView);
        ItemUtil.updateName(item, headerMatrixMap, itemService);
        ItemUtil.updateDescription(item, headerMatrixMap, itemService);
        ItemUtil.updateChildItems(item, itemView, itemService, currentUser);
        ItemUtil.updateReplacers(item, itemView, itemService, currentUser);
        ItemUtil.updateWishList(item, itemView, currentUser, itemService);

        itemService.update(item);

        return createItemView(itemId, currentUser.getName());
    }

    private String createEmptyDescription(final String category) {
        final List<Item> items = itemService.find(category);
        if (items.isEmpty()) {
            return "";
        }
        final Map<String, String> descriptionMap = ItemUtil.toMap(items.get(0).getDescription());
        for (final Map.Entry<String, String> entry : descriptionMap.entrySet()) {
            entry.setValue("-");
        }
        return ItemUtil.toDescription(descriptionMap);
    }

    private ItemView removeItemFromWishList(final ItemView itemView, final UserEntity user) {
        for (final Long itemId : itemView.getIdsToRemove()) {
            final Item item = itemService.getOne(itemId);
            user.getWishList().getItems().remove(item);
            UserActionUtil.processItemAction("remove from wishlist", item, user, itemService);
        }
        itemService.getUserService().update(user);
        itemView.getIdsToRemove().clear();
        return itemView;
    }

    private ItemView removeUsers(final ItemView itemView) {
        for (final Long userToRemoveId : itemView.getIdsToRemove()) {
            itemService.getUserService().delete(userToRemoveId);
        }
        itemView.getIdsToRemove().clear();
        return itemView;
    }

    private ItemView removeItem(final ItemView itemView, final UserEntity user, final UserService userService) {
        removeItems(itemView.getIdsToRemove(), user, userService);
        itemView.getIdsToRemove().clear();
        return itemView;
    }

    private void removeItems(final Set<Long> idsToRemove,
                             final UserEntity currentUser,
                             final UserService userService) {
        for (final Long idToRemove : idsToRemove) {
            final Item itemToRemove = itemService.getOne(idToRemove);
            removeItemFromAllWishLists(itemToRemove, userService);
            removeItemFromAllParentItems(idToRemove, currentUser);
            removeItem(itemToRemove, currentUser);
        }
    }

    private void removeItem(final Item itemToRemove, final UserEntity user) {
        itemToRemove.setStatus("deleted");
        itemToRemove.setUserActionDate(DateUtil.now());
        itemService.update(itemToRemove);
        UserActionUtil.processItemAction("delete", itemToRemove, user, itemService);
    }

    private void removeItemFromAllWishLists(final Item itemToRemove, final UserService userService) {
        for (final UserEntity user : userService.findAll()) {
            user.getWishList().getItems().remove(itemToRemove);
            userService.update(user);
        }
    }

    private void removeItemFromAllParentItems(final Long idToRemove, final UserEntity user) {
        final String actionType = "delete";

        for (final Item item : itemService.findAll()) {
            for (final Replacer replacer : item.getReplacers()) {
                final Item nestedItem = replacer.getItem();
                if (nestedItem.getId().equals(idToRemove)) {
                    replacer.setStatus("deleted");
                    itemService.getReplacerRepository().save(replacer);
                    UserActionUtil.processReplacerAction(actionType, replacer, item, user, itemService);
                }
            }

            for (final ChildItem part : item.getChildItems()) {
                final Item nestedItem = part.getItem();
                if (nestedItem.getId().equals(idToRemove)) {
                    part.setStatus("deleted");
                    itemService.getChildItemRepository().save(part);
                    UserActionUtil.processPartAction(actionType, part, item, user, itemService);
                }
            }

            itemService.update(item);
        }
    }

}
