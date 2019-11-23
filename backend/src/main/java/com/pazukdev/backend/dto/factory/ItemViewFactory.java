package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pazukdev.backend.util.ItemUtil.SpecialItemId.*;
import static com.pazukdev.backend.util.NestedItemUtil.createPossibleParts;
import static com.pazukdev.backend.util.NestedItemUtil.createReplacerDtos;
import static com.pazukdev.backend.util.TableUtil.*;
import static com.pazukdev.backend.util.TranslatorUtil.translate;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public class ItemViewFactory {

    private final ItemService itemService;

    public ItemView createItemView(final Long itemId, final String userName, final String userLanguage) {
        final UserEntity currentUser = itemService.getUserService().findByName(userName);
        final WishList wishList = currentUser.getWishList();

        final ItemView basicItemView = new ItemView();
        basicItemView.setItemId(itemId);
        basicItemView.setWishListIds(UserUtil.collectWishListItemsIds(currentUser));
        basicItemView.setUserData(NestedItemDtoFactory.createUser(currentUser));

        ItemView itemView;

        if (itemId == WISH_LIST_VIEW.getItemId()) {
            itemView = createWishListView(basicItemView, wishList);
        } else if (itemId == MOTORCYCLE_CATALOGUE_VIEW.getItemId()) {
            itemView = createMotorcycleCatalogueView(basicItemView);
        } else if (itemId == ITEMS_MANAGEMENT_VIEW.getItemId()) {
            itemView = createItemsManagementView(basicItemView);
        } else if (itemId == USER_LIST_VIEW.getItemId()) {
            itemView = createUsersListView(basicItemView);
        } else {
            itemView = createOrdinaryItemView(basicItemView, itemId, currentUser, userLanguage);
        }

        if (!userLanguage.equals("en")) {
            translate("en", userLanguage, itemView, false, itemService);
        }
        return itemView;
    }

    public ItemView createNewItemView(final String category,
                                      final String name,
                                      final String userName,
                                      final String userLanguage) {
        final Item item = createNewItem(name, category, userName, userLanguage);
        final ItemView itemView = createItemView(item.getId(), userName, userLanguage);
        itemView.setNewItem(true);
        return itemView;
    }

    private Item createNewItem(String name,
                               String category,
                               final String userName,
                               final String userLanguage) {
        final UserEntity creator = itemService.getUserService().findByName(userName);

        if (!userLanguage.equals("en")) {
            name = translate(userLanguage, "en", name, true, false, itemService);
            category = translate(userLanguage, "en", category, true, false, itemService);
        }

        final Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setCreatorId(creator.getId());
        item.setUserActionDate(DateUtil.now());
        item.setDescription(createEmptyDescription(category));
        itemService.update(item);
        UserActionUtil.processItemAction("create", item, creator, itemService);
        return item;
    }

    public ItemView updateItemView(final Long itemId,
                                   final String userName,
                                   final String userLanguage,
                                   final ItemView itemView) {
        final UserEntity user = itemService.getUserService().findByName(userName);
        final boolean removeItem = itemId == ITEMS_MANAGEMENT_VIEW.getItemId();
        final boolean removeItemFromWishList = itemId == WISH_LIST_VIEW.getItemId();
        final boolean removeUser = itemId == USER_LIST_VIEW.getItemId();

        if (removeItem) {
            return removeItem(itemView, user, itemService.getUserService(), itemView.isHardDelete());
        }
        if (removeItemFromWishList) {
            return removeItemFromWishList(itemView, user);
        }
        if (removeUser) {
            return removeUsers(itemView);
        }
        return updateItem(itemId, itemView, user, userLanguage);
    }

    private ItemView createOrdinaryItemView(final ItemView itemView,
                                            final Long itemId,
                                            final UserEntity currentUser,
                                            final String language) {
        final Item item = itemService.getOne(itemId);
        final List<Item> allItems = itemService.findAll();
        final List<Item> sameCategoryItems = itemService.find(item.getCategory(), allItems);
        final String tableName = "Parts";
        final String itemCategory = item.getCategory();
        final ImgViewData imgViewData = ImgUtil.getImgViewData(item);

        itemView.setSearchEnabled(true);
        itemView.setCategory(itemCategory);
        itemView.setDefaultImg(imgViewData.isDefaultImg());
        itemView.setImgData(imgViewData.getImgData());
        itemView.setHeader(createHeader(item, itemService));
        itemView.setPartsTable(createPartsTable(item, tableName, itemService));
        itemView.setReplacersTable(createReplacersTable(item, itemService.getUserService()));
        itemView.getPossibleParts().addAll(createPossibleParts(allItems, itemService.getUserService()));
        itemView.getReplacers().addAll(createReplacerDtos(sameCategoryItems, itemService.getUserService()));
        itemView.setCreatorId(item.getCreatorId());
        itemView.setCreatorName(UserUtil.getCreatorName(item, itemService.getUserService()));
        itemView.getRatedItems().addAll(UserUtil.collectRatedItemIds(currentUser));
        return itemView;
    }

    private ItemView createMotorcycleCatalogueView(final ItemView itemView) {
        final List<Item> motorcycles = itemService.find("Motorcycle");
        final String tableName = "Motorcycle catalogue";
        final String countParameterName = "Model";

        itemView.setImgData(ImgUtil.getAppImgData());

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
        String tableName = "Your Wishlist";
        String countParameterName = "Items";

        return createItemsView(
                itemView,
                allItems.size(),
                tableName,
                countParameterName,
                specialItemsTable(allItems, countParameterName, itemService));
    }

    private ItemView createItemsView(final ItemView itemView,
                                     final Integer size,
                                     String tableName,
                                     String parameter,
                                     final PartsTable table) {
        final String itemCategory = "-";
        final HeaderTableRow row = HeaderTableRow.create(parameter, String.valueOf(size), itemCategory);
        final HeaderTable header = HeaderTable.createSingleRowTable(tableName, row);
        final List<String> categories = new ArrayList<>(itemService.findAllCategories());

        itemView.setHeader(header);
        itemView.setPartsTable(table);
        itemView.setReplacersTable(ReplacersTable.createStub());
        itemView.setAllCategories(categories);
        return itemView;
    }

    private ItemView updateItem(final Long itemId,
                                final ItemView itemView,
                                final UserEntity currentUser,
                                final String userLanguage) {

        final Item item = itemService.getOne(itemId);

        if (itemView.getRate() != null) {
            RateUtil.processRateItemAction(itemView, currentUser, itemService);
            itemView.setRate(null);
            return createItemView(itemId, currentUser.getName(), userLanguage);
        }

        if (itemView.isAddToWishList()) {
            ItemUtil.updateWishList(item, itemView, currentUser, itemService);
        } else {
            if (!userLanguage.equals("en")) {
                translate(userLanguage, "en", itemView, true, itemService);
            }

            final Map<String, String> headerMap = TableUtil.createHeaderMap(itemView.getHeader());

            ItemUtil.updateName(item, headerMap, itemService);
            ItemUtil.updateDescription(item, headerMap, itemService);
            ItemUtil.updateImg(itemView, item);
            ItemUtil.updateChildItems(item, itemView, itemService, currentUser);
            ItemUtil.updateReplacers(item, itemView, itemService, currentUser);
        }

        itemService.update(item);
        return createItemView(itemId, currentUser.getName(), userLanguage);
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

    private ItemView removeItem(final ItemView itemView,
                                final UserEntity user,
                                final UserService userService,
                                final boolean hardDelete) {
        removeItems(itemView.getIdsToRemove(), user, userService, hardDelete);
        itemView.getIdsToRemove().clear();
        return itemView;
    }

    private void removeItems(final Set<Long> idsToRemove,
                             final UserEntity currentUser,
                             final UserService userService,
                             final boolean hardDelete) {
        for (final Long idToRemove : idsToRemove) {
            final Item itemToRemove = itemService.getOne(idToRemove);
            removeItemFromAllWishLists(itemToRemove, userService);
            removeItemFromAllParentItems(idToRemove, currentUser, hardDelete);
            removeItem(itemToRemove, currentUser, hardDelete);
        }
    }

    private void removeItem(final Item itemToRemove, final UserEntity user, final boolean hardDelete) {
        if (hardDelete) {
            final Long id = itemToRemove.getId();
            itemService.delete(id);
//            itemService.delete(id);
//            Set<Replacer> replacersToRemove = new HashSet<>();
//            for (final Replacer replacer : itemService.getReplacerRepository().findAll()) {
//                if (replacer.getItem().getId().equals(id)) {
//                    replacersToRemove.add(replacer);
//                }
//            }
//            for (Replacer replacer : replacersToRemove) {
//                itemService.getReplacerRepository().deleteById(replacer.getId());
//            }
            return;
        }
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

    private void removeItemFromAllParentItems(final Long idToRemove, final UserEntity user, boolean hardDelete) {
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

            if (hardDelete) {
                hardDeleteItemsInSet(item.getReplacers());
                hardDeleteItemsInSet(item.getChildItems());
            }

            itemService.update(item);
        }
    }

    private <E extends AbstractEntity> void hardDeleteItemsInSet(final Set<E> set) {
        set.removeIf(abstractEntity -> abstractEntity.getStatus().equals("deleted"));
    }

}
