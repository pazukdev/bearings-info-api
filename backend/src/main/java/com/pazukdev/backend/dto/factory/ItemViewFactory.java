package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.HeaderTableRow;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.*;
import lombok.RequiredArgsConstructor;

import java.util.*;

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

    private final static List<String> partsDisabled = new ArrayList<>(Arrays
            .asList("manufacturer", "material", "standard", "wire"));

    private final ItemService itemService;

    public ItemView createHomeView(final String userName, final String userLanguage) {
        return createItemView(MOTORCYCLE_CATALOGUE_VIEW.getItemId(), userName, userLanguage);
    }

    public ItemView createItemsManagementView(final String userName, final String userLanguage) {
        return createItemView(ITEMS_MANAGEMENT_VIEW.getItemId(), userName, userLanguage);
    }

    public ItemView createWishlistView(final String userName, final String userLanguage) {
        return createItemView(WISH_LIST_VIEW.getItemId(), userName, userLanguage);
    }

    public ItemView createUserListView(final String userName, final String userLanguage) {
        return createItemView(USER_LIST_VIEW.getItemId(), userName, userLanguage);
    }

    public ItemView createItemView(final Long itemId, final String userName, final String userLanguage) {
        final long businessLogicStartTime = System.nanoTime();

        final UserEntity currentUser = itemService.getUserService().findByName(userName);
        final WishList wishList = currentUser.getWishList();

        final ItemView basicItemView = new ItemView();
        basicItemView.setItemId(itemId);
        basicItemView.setWishListIds(UserUtil.collectWishListItemsIds(currentUser));
        basicItemView.setUserData(UserDtoFactory.createItemViewUserData(currentUser));

        ItemView itemView;

        if (itemId.equals(WISH_LIST_VIEW.getItemId())) {
            itemView = createWishListView(basicItemView, wishList);
        } else if (itemId.equals(MOTORCYCLE_CATALOGUE_VIEW.getItemId())) {
            itemView = createMotorcycleCatalogueView(basicItemView);
        } else if (itemId.equals(ITEMS_MANAGEMENT_VIEW.getItemId())) {
            itemView = createItemsManagementView(basicItemView);
        } else if (itemId.equals(USER_LIST_VIEW.getItemId())) {
            itemView = createUsersListView(basicItemView);
        } else {
            itemView = createOrdinaryItemView(basicItemView, itemId, currentUser);
        }

        final double businessLogicEndTime = System.nanoTime();
        final double businessLogicDuration = businessLogicEndTime - businessLogicStartTime;

        if (!userLanguage.equals("en")) {
            translate("en", userLanguage, itemView, false, itemService);
        }
        double translationDuration = System.nanoTime() - businessLogicEndTime;

        setTime(itemView, businessLogicDuration, translationDuration);
        return itemView;
    }

    public ItemView createNewItemView(final String category,
                                      final String name,
                                      final String userName,
                                      final String userLanguage) {
        final long businessLogicStartTime = System.nanoTime();

        final Item item = createNewItem(name, category, userName, userLanguage);
        final ItemView itemView = createItemView(item.getId(), userName, userLanguage);
        itemView.setNewItem(true);

        setTime(itemView, (double) (System.nanoTime() - businessLogicStartTime), null);
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
        item.setImage("-");
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
        final boolean removeItem = itemId.equals(ITEMS_MANAGEMENT_VIEW.getItemId());
        final boolean removeItemFromWishList = itemId.equals(WISH_LIST_VIEW.getItemId());
        final boolean removeUser = itemId.equals(USER_LIST_VIEW.getItemId());

        if (removeItem) {
            return removeItem(itemView, user, itemService.getUserService(), itemView.isHardDelete());
        }
        if (removeItemFromWishList) {
            return editWishList(itemView, user, userLanguage);
        }
        if (removeUser) {
            return removeUsers(itemView);
        }
        return updateItem(itemId, itemView, user, userLanguage);
    }

    private ItemView createOrdinaryItemView(final ItemView itemView,
                                            final Long itemId,
                                            final UserEntity currentUser) {
        final Item item = itemService.getOne(itemId);
        final List<Item> allItems = itemService.findAll();
        final List<Item> sameCategoryItems = itemService.find(item.getCategory(), allItems);
        final String category = item.getCategory();
        final String name = item.getName();
        final ImgViewData imgViewData = ImgUtil.getImgViewData(item);

        itemView.setSearchEnabled(true);
        itemView.setCategory(category);
        itemView.setLocalizedCategory(category);
        itemView.setName(name);
        itemView.setLocalizedName(name);
        itemView.setDefaultImg(imgViewData.isDefaultImg());
        itemView.setImgData(imgViewData.getImgData());
        itemView.setHeader(createHeader(item, itemService));
        itemView.setPartsTable(createPartsTable(item, itemService));
        itemView.setReplacersTable(createReplacersTable(item, itemService.getUserService()));
        itemView.getPossibleParts().addAll(createPossibleParts(allItems, category, itemService.getUserService()));
        itemView.getPossibleReplacers().addAll(createReplacerDtos(sameCategoryItems, itemId, itemService.getUserService()));
        itemView.setCreatorId(item.getCreatorId());
        itemView.setCreatorName(UserUtil.getCreatorName(item, itemService.getUserService()));
        itemView.setLikeList(UserUtil.createLikeListDto(currentUser));
        itemView.setPartsEnabled(!partsDisabled.contains(category.toLowerCase()));
        LinkUtil.setLinksToItemView(itemView, item);

        return itemView;
    }

    private ItemView createMotorcycleCatalogueView(final ItemView itemView) {
        final List<Item> motorcycles = itemService.find("Motorcycle");
        final String countParameterName = "Model";

        itemView.setLocalizedName("Motorcycle catalogue");
        itemView.setImgData(ImgUtil.getAppImgData());

        return createItemsView(
                itemView,
                motorcycles.size(),
                countParameterName,
                motorcyclesTable(motorcycles, itemService.getUserService()));
    }

    private ItemView createUsersListView(final ItemView itemView) {
        final List<UserEntity> users = itemService.getUserService().findAll();
        final String countParameterName = "User";

        itemView.setLocalizedName("Users");

        return createItemsView(
                itemView,
                users.size(),
                countParameterName,
                usersTable(users));
    }

    private ItemView createItemsManagementView(final ItemView itemView) {
        final List<Item> allItems = itemService.findAll();
        final String countParameterName = "Items";

        itemView.setLocalizedName("Items management");

        return createItemsView(
                itemView,
                allItems.size(),
                countParameterName,
                specialItemsTable(allItems, itemService));
    }

    private ItemView createWishListView(final ItemView itemView, final WishList wishList) {
        final Set<ChildItem> allItems = wishList.getItems();
        String countParameterName = "Items";

        itemView.setLocalizedName("Your Wishlist");

        return createItemsView(
                itemView,
                allItems.size(),
                countParameterName,
                wishListTable(allItems, itemService));
    }

    private ItemView createItemsView(final ItemView itemView,
                                     final Integer size,
                                     String parameter,
                                     final PartsTable table) {
        final HeaderTableRow row = HeaderTableRow.create(parameter, String.valueOf(size));
        final HeaderTable header = HeaderTable.createSingleRowTable("-", row);
        final List<String> categories = new ArrayList<>(itemService.findAllCategories());

        itemView.setHeader(header);
        itemView.setPartsTable(table);
        itemView.setReplacersTable(ReplacersTable.createStub());
        itemView.setAllCategories(categories);
        return itemView;
    }

    private ItemView updateItem(final Long itemId,
                                final ItemView view,
                                final UserEntity currentUser,
                                final String userLanguage) {

        final long businessLogicStartTime = System.nanoTime();

        final Item item = itemService.getOne(itemId);

        final long translationFromUserLang = System.nanoTime();
        if (!userLanguage.equals("en")) {
            translate(userLanguage, "en", view, true, itemService);
        }
        final long translationFromUserLangDuration = System.nanoTime() - translationFromUserLang;

        final Map<String, String> headerMap = TableUtil.createHeaderMap(view.getHeader());

        ItemUtil.updateName(item, headerMap, itemService);
        ItemUtil.updateDescription(item, headerMap, itemService);
        ImgUtil.updateImg(view, item);
        ItemUtil.updateChildItems(item, view, itemService, currentUser);
        ItemUtil.updateReplacers(item, view, itemService, currentUser);
        LinkUtil.updateItemLinks(item, view);
        itemService.update(item);

        final ItemView newItemView = createItemView(itemId, currentUser.getName(), userLanguage);

        final double totalTranslationTime = view.getTranslationTime() * 1000000000 + translationFromUserLangDuration;
        setTime(newItemView, (double) (System.nanoTime() - businessLogicStartTime), null);
        return newItemView;
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

    private ItemView editWishList(final ItemView itemView, final UserEntity user, final String userLanguage) {
        final Set<ChildItem> oldItems = new HashSet<>(user.getWishList().getItems());

        Set<ChildItem> newItems = ChildItemUtil.createPartsFromItemView(itemView, itemService);
        itemView.getHeader().getRows().get(0).setValue(String.valueOf(newItems.size()));

        // delete all items from wishlist and child item repository and save that state
        user.getWishList().getItems().clear();
        itemService.getUserService().update(user);
        for (final ChildItem oldItem : oldItems) {
            if (!newItems.contains(oldItem)) {
                final Long id = oldItem.getId();
                itemService.getChildItemRepository().deleteById(id);
            }
        }

        // add all actual items to wishlist after translation and save that state
        final List<NestedItemDto> dtos = itemView.getPartsTable().getParts();
        TranslatorUtil.translateNestedItemDtoList(userLanguage, "en", dtos, true, itemService);
        newItems = ChildItemUtil.createPartsFromItemView(itemView, itemService);
        user.getWishList().getItems().addAll(newItems);
        itemService.getUserService().update(user);

        itemView.setWishListIds(ChildItemUtil.collectIds(newItems));
        TranslatorUtil.translateNestedItemDtoList("en", userLanguage, dtos, true, itemService);
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
            final List<ChildItem> wishListItemsCopy = new ArrayList<>(user.getWishList().getItems());
            for (final ChildItem wishListItem : wishListItemsCopy) {
                if (wishListItem.getItem().getId().equals(itemToRemove.getId())) {
                    user.getWishList().getItems().remove(wishListItem);
                }
            }
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

    private void setTime(final ItemView view,
                         final Double businessLogicDuration,
                         final Double translationDuration) {
        final double secondsInNano = 0.000000001;
        if (businessLogicDuration != null) {
            view.setBusinessLogicTime(businessLogicDuration * secondsInNano);
        }
        if (translationDuration != null) {
            view.setTranslationTime(translationDuration * secondsInNano);
        }
        view.setResponseTotalTime(view.getBusinessLogicTime() + view.getTranslationTime());
    }

}
