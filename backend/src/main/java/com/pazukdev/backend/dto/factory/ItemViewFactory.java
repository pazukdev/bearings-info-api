package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.DateUtil;
import com.pazukdev.backend.util.ImgUtil;
import com.pazukdev.backend.util.LinkUtil;
import com.pazukdev.backend.util.TableUtil;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.pazukdev.backend.dto.DictionaryData.getDictionaryFromFile;
import static com.pazukdev.backend.dto.DictionaryData.saveDictionary;
import static com.pazukdev.backend.dto.factory.NestedItemDtoFactory.*;
import static com.pazukdev.backend.util.CategoryUtil.Category.VEHICLE;
import static com.pazukdev.backend.util.CategoryUtil.Parameter;
import static com.pazukdev.backend.util.ChildItemUtil.collectIds;
import static com.pazukdev.backend.util.ChildItemUtil.createChildrenFromItemView;
import static com.pazukdev.backend.util.FileUtil.FileName;
import static com.pazukdev.backend.util.FileUtil.getTxtFileTextLines;
import static com.pazukdev.backend.util.ItemUtil.SpecialItemId.*;
import static com.pazukdev.backend.util.ItemUtil.*;
import static com.pazukdev.backend.util.NestedItemUtil.addPossiblePartsAndReplacers;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.TableUtil.createHeader;
import static com.pazukdev.backend.util.TableUtil.createReplacersTable;
import static com.pazukdev.backend.util.TranslatorUtil.isValid;
import static com.pazukdev.backend.util.TranslatorUtil.translate;
import static com.pazukdev.backend.util.UserActionUtil.*;
import static com.pazukdev.backend.util.UserUtil.createLikeListDto;
import static com.pazukdev.backend.util.UserUtil.getCreatorData;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public class ItemViewFactory {

    private final ItemService itemService;
    private final List<String> infoCategories;

    public ItemView createHomeView(final String userName, final String userLanguage) {
        return createItemView(VEHICLES_VIEW.getItemId(), Status.ACTIVE, userName, userLanguage);
    }

    public ItemView createItemsListView(final String itemsStatus, final String userName, final String userLanguage) {
        return createItemView(ITEMS_MANAGEMENT_VIEW.getItemId(), itemsStatus, userName, userLanguage);
    }

    public ItemView createWishlistView(final String userName, final String userLanguage) {
        return createItemView(WISH_LIST_VIEW.getItemId(), Status.ACTIVE, userName, userLanguage);
    }

    public ItemView createUserListView(final String userName, final String userLang) {
        return createItemView(USER_LIST_VIEW.getItemId(), Status.ACTIVE, userName, userLang);
    }

    public ItemView createItemView(final Long itemId,
                                   final String status,
                                   String userName,
                                   final String userLang) {
        final long businessLogicStartTime = System.nanoTime();

        final UserService userService = itemService.getUserService();
        if (isEmpty(userName) || userName.equals("undefined")) {
            userName = Role.GUEST.name().toLowerCase();
        }
        final UserEntity currentUser = userService.findFirstByName(userName);
        final WishList wishList = currentUser.getWishList();

        final ItemView basicView = new ItemView();
        basicView.setItemId(itemId);
        basicView.setWishListIds(collectIds(wishList.getItems()));
        basicView.setUserData(UserDtoFactory.createItemViewUserData(currentUser));

        ItemView view;

        boolean userListView = itemId.equals(USER_LIST_VIEW.getItemId());

        if (itemId.equals(WISH_LIST_VIEW.getItemId())) {
            view = createWishListView(basicView, wishList, userService);
        } else if (itemId.equals(VEHICLES_VIEW.getItemId())) {
            view = createVehiclesView(basicView, userService);
        } else if (itemId.equals(ITEMS_MANAGEMENT_VIEW.getItemId())) {
            view = createItemsListView(basicView, status);
        } else if (userListView) {
            view = createUsersListView(basicView, userService);
        } else {
            view = createOrdinaryItemView(basicView, itemId, currentUser, userService);
        }

        final double businessLogicEndTime = System.nanoTime();
        final double businessLogicDuration = businessLogicEndTime - businessLogicStartTime;

        if (!userLang.equals("en") && !userListView && isValid(userLang)) {
            try {
                translate("en", userLang, view, false);
            } catch (Exception e) {
                view.setErrorMessage(e.getMessage());
                return view;
            }
        }
        double translationDuration = System.nanoTime() - businessLogicEndTime;

        setTime(view, businessLogicDuration, translationDuration);
        return view;
    }

    public ItemView createNewItemView(final String category,
                                      final String name,
                                      final String userName,
                                      final String userLanguage) throws Exception {
        final long businessLogicStartTime = System.nanoTime();

        final Item item = createNewItem(name, category, userName, userLanguage);
        final ItemView view = createItemView(item.getId(), Status.ACTIVE, userName, userLanguage);
        view.setNewItem(true);

        setTime (view, (double) (System.nanoTime() - businessLogicStartTime), null);
        return view;
    }

    private Item createNewItem(String name,
                               String category,
                               final String userName,
                               final String userLang) throws Exception {
        final UserEntity creator = itemService.getUserService().findFirstByName(userName);

        if (!userLang.equals("en") && isValid(userLang)) {
            final DictionaryData dictionaryData = getDictionaryFromFile(userLang);
            final List<String> dictionary = dictionaryData.getDictionary();
            name = translate(userLang, "en", name, true, false, dictionary);
            category = translate(userLang, "en", category, false, true, dictionary);
            saveDictionary(dictionaryData);
        }

        final Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setImg("-");
        item.setCreatorId(creator.getId());
        item.setUserActionDate(DateUtil.now());
        item.setDescription(createEmptyDescription(category, itemService.getItemRepository()));
        itemService.update(item);
        processItemAction(ActionType.CREATE, item, creator, itemService);
        return item;
    }

    public ItemView updateItemView(final Long itemId,
                                   final String userName,
                                   final String userLanguage,
                                   final ItemView view) {
        final UserEntity user = itemService.getUserService().findFirstByName(userName);
        final boolean removeItem = itemId.equals(ITEMS_MANAGEMENT_VIEW.getItemId());
        final boolean removeItemFromWishList = itemId.equals(WISH_LIST_VIEW.getItemId());
        final boolean removeUser = itemId.equals(USER_LIST_VIEW.getItemId());

        if (removeItem) {
            return removeItem(view, user, itemService.getUserService());
        }
        if (removeItemFromWishList) {
            return editWishList(view, user);
        }
        if (removeUser) {
            return removeUsers(view);
        }
        return updateItem(itemId, view, user, userLanguage);
    }

    private ItemView createOrdinaryItemView(final ItemView view,
                                            final Long itemId,
                                            final UserEntity currentUser,
                                            final UserService userService) {
        final Item item = itemService.findOne(itemId);
        final List<Item> allItems = itemService.findAll();
        allItems.remove(item);
        final String category = item.getCategory();
        final String name = item.getName();
        final ImgViewData imgViewData = ImgUtil.getImg(item);
        final Map<String, String> description = toMap(item.getDescription());

        view.setSearchEnabled(true);
        view.setOrdinaryItem(true);
        view.setCategory(category);
        if (category.equals(VEHICLE)) {
            view.setVehicleClass(description.get(Parameter.CLASS));
        }
        view.setStatus(item.getStatus());
        view.setLocalizedCategory(category);
        view.setName(name);
        view.setLocalizedName(name);
        view.setDefaultImg(imgViewData.getDefaultImg());
        view.setImg(imgViewData.getImg());
        view.setHeader(createHeader(item, description, itemService));
        view.setChildren(createChildren(item, userService, false));
        view.setAllChildren(createChildren(item, userService, true));
        view.setReplacersTable(createReplacersTable(item, userService));
        addPossiblePartsAndReplacers(view, allItems, item, infoCategories, itemService);
        view.setCreatorData(getCreatorData(item, itemService.getUserService()));
        view.setLikeList(createLikeListDto(currentUser));
        LinkUtil.setLinksToItemView(view, item);
        view.setParents(createParentItemsView(item, userService, allItems));
        return view;
    }

    private static List<NestedItemDto> createChildren(final Item item,
                                                      final UserService userService,
                                                      final boolean all) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        addParts(item.getChildItems(), dtos, userService, all, null);
        return dtos;
    }

    public static void addParts(final Set<ChildItem> parts,
                                final List<NestedItemDto> dtos,
                                final UserService userService,
                                final boolean summary,
                                final Double parentQuantity) {
        for (final ChildItem part : parts) {
            boolean add = true;
            final NestedItemDto partDto = createChildItem(part, userService, !summary);
            Double quantity = null;
            if (summary) {
                quantity = getFirstNumber(part.getQuantity());
                if (quantity != null && parentQuantity != null) {
                    quantity = quantity * parentQuantity;
                    partDto.setSecondComment(doubleToString(quantity));
                }
                for (final NestedItemDto dto : dtos) {
                    final boolean itemIsInList = dto.getItemId().equals(partDto.getItemId());
                    if (itemIsInList) {
                        final String totalQuantity = sumQuantities(dto.getSecondComment(), doubleToString(quantity));
                        dto.setSecondComment(totalQuantity);
                        add = false;
                        break;
                    }
                }
            }
            if (add) {
                dtos.add(partDto);
            }
            if (summary) {
                addParts(part.getItem().getChildItems(), dtos, userService, true, quantity);
            }
        }
    }

    private ItemView createVehiclesView(final ItemView view, final UserService userService) {
        final List<Item> vehicles = itemService.find(VEHICLE);

        final List<NestedItemDto> dtos = new ArrayList<>();
        vehicles.forEach(vehicle -> dtos.add(createVehicle(vehicle, userService)));

        view.setChildren(dtos);
        view.setAdminMessage(AdminMessage.getMessage(itemService.getAdminMessageRepository()));
        view.setLastVehicles(getLastNewVehicles(itemService));
        view.setLastReplacers(getLastNewReplacers(itemService));
        return view;
    }

    private ItemView createItemsListView(final ItemView view, final String itemsStatus) {
        final List<Item> items = itemService.findAll(itemsStatus);
        final List<String> comments = getTxtFileTextLines(FileName.COMMENTS);

        final List<NestedItemDto> dtos = new ArrayList<>();
        items.forEach(item -> dtos.add(createItemForItemsManagement(item, itemService.getUserService(), comments)));

        view.setChildren(dtos);
        view.setAllCategories(new ArrayList<>(itemService.collectCategories(items)));
        return view;
    }

    private ItemView createParentItemsView(final Item item,
                                           final UserService userService,
                                           final List<Item> allItems) {
        final List<String> comments = getTxtFileTextLines(FileName.COMMENTS);

        final ItemView view = new ItemView();
        final List<NestedItemDto> dtos = new ArrayList<>();
        itemService.findParents(item, allItems, infoCategories)
                .forEach(parent -> dtos.add(createItemForItemsManagement(parent, userService, comments)));
        view.setChildren(dtos);
        return view;
    }

    private ItemView createUsersListView(final ItemView view, final UserService userService) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        userService.findAll().forEach(user -> dtos.add(createUser(user)));
        view.setChildren(dtos);
        return view;
    }

    private ItemView createWishListView(final ItemView view, final WishList wishList, final UserService userService) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        wishList.getItems().forEach(item -> dtos.add(createWishListItem(item, userService)));
        view.setChildren(dtos);
        return view;
    }

    private ItemView updateItem(final Long itemId,
                                final ItemView view,
                                final UserEntity currentUser,
                                final String userLang) {

        final long businessLogicStartTime = System.nanoTime();

        final long translationFromUserLang = System.nanoTime();
        if (!userLang.equals("en") && isValid(userLang)) {
            try {
                translate(userLang, "en", view, true);
            } catch (Exception e) {
                view.setErrorMessage(e.getMessage());
                return view;
            }
        }
        final long translationFromUserLangDuration = System.nanoTime() - translationFromUserLang;

        final HeaderTable header = view.getHeader();
        final String newName = header.getValue(Parameter.DescriptionIgnored.NAME);
        final String newCategory = header.getValue(Parameter.DescriptionIgnored.CATEGORY);
        header.removeRow(Parameter.DescriptionIgnored.NAME);
        header.removeRow(Parameter.DescriptionIgnored.CATEGORY);

        final Map<String, String> newDescriptionMap = TableUtil.createHeaderMap(header);
        final String newDescription = toDescription(newDescriptionMap);

        final Item oldItem = itemService.findOne(itemId);
        final List<Item> allItems = itemService.findAll();
        allItems.remove(oldItem);

        boolean moveItemToAnotherCategory = updateNameAndCategory(oldItem, newCategory, newName, allItems, infoCategories, itemService);

        if (moveItemToAnotherCategory) {
            moveItemToAnotherCategory(oldItem, newCategory, newDescriptionMap, itemService);
        } else if (!oldItem.getDescription().equals(newDescription)) {
            oldItem.setDescription(newDescription);
            applyNewDescriptionToCategory(newCategory, header, newDescriptionMap, allItems, itemService);
        }
        ImgUtil.updateImg(view, oldItem);
        updateChildItems(oldItem, view, itemService, currentUser);
        updateReplacers(oldItem, view, itemService, currentUser);
        LinkUtil.updateItemLinks(oldItem, view, currentUser, itemService);
        oldItem.setStatus(view.getStatus());

        itemService.update(oldItem);

        final ItemView newItemView = createItemView(itemId, oldItem.getStatus(), currentUser.getName(), userLang);

        final double totalTranslationTime = view.getTranslationTime() * 1000000000 + translationFromUserLangDuration;
        setTime(newItemView, (double) (System.nanoTime() - businessLogicStartTime), totalTranslationTime);
        return newItemView;
    }

    private ItemView editWishList(final ItemView view, final UserEntity user) {
        final Set<ChildItem> newWishListItems = createChildrenFromItemView(view, itemService);
        final Set<ChildItem> toRemove = new HashSet<>();
        for (final ChildItem oldWishListItem : user.getWishList().getItems()) {
            boolean remove = true;
            for (final ChildItem newWishListItem : newWishListItems) {
                final Long newItemId = newWishListItem.getItem().getId();
                final Long oldItemId = oldWishListItem.getItem().getId();
                final boolean updateOldWishListItem = oldItemId.equals(newItemId);
                if (updateOldWishListItem) {
                    oldWishListItem.setLocation(newWishListItem.getLocation());
                    oldWishListItem.setQuantity(newWishListItem.getQuantity());
                    remove = false;
                }
            }
            if (remove) {
                toRemove.add(oldWishListItem);
            }
        }

        user.getWishList().getItems().removeAll(toRemove);
        itemService.getUserService().update(user);
        view.setWishListIds(collectIds(newWishListItems));
        return view;
    }

    private ItemView removeUsers(final ItemView view) {
        for (final Long userToRemoveId : view.getIdsToRemove()) {
            itemService.getUserService().softDelete(userToRemoveId);
        }
        view.getIdsToRemove().clear();
        return view;
    }

    private ItemView removeItem(final ItemView view,
                                final UserEntity user,
                                final UserService userService) {
        removeItems(view.getIdsToRemove(), user, userService);
        view.getIdsToRemove().clear();
        view.setWishListIds(collectIds(user.getWishList().getItems()));
        return view;
    }

    private void removeItems(final Set<Long> idsToRemove,
                             final UserEntity currentUser,
                             final UserService userService) {
        for (final Long idToRemove : idsToRemove) {
            final Item itemToRemove = itemService.findOne(idToRemove);
            removeItemFromAllWishLists(itemToRemove, userService);
            removeItemFromAllParentItems(idToRemove, currentUser);
            removeItem(itemToRemove, currentUser);
        }
    }

    private void removeItem(final Item itemToRemove, final UserEntity user) {
        final String status = itemToRemove.getStatus();
        if (status.equals(Status.DELETED)) {
            itemService.hardDelete(itemToRemove.getId());
            return;
        }
        itemToRemove.setStatus(Status.DELETED);
        itemToRemove.setUserActionDate(DateUtil.now());
        itemService.update(itemToRemove);
        processItemAction(ActionType.DELETE, itemToRemove, user, itemService);
    }

    private void removeItemFromAllWishLists(final Item itemToRemove, final UserService userService) {
        for (final UserEntity user : userService.findAll()) {
            user.getWishList().getItems()
                    .removeIf(wishListItem -> wishListItem.getItem().getId().equals(itemToRemove.getId()));
            userService.update(user);
        }
    }

    private void removeItemFromAllParentItems(final Long idToRemove, final UserEntity user) {
        for (final Item item : itemService.findAll()) {
            for (final Replacer replacer : new ArrayList<>(item.getReplacers())) {
                final Item nestedItem = replacer.getItem();
                if (nestedItem.getId().equals(idToRemove)) {
//                    replacer.setStatus("deleted");
                    item.getReplacers().remove(replacer);
                    processReplacerAction(ActionType.DELETE, replacer, item, user, itemService);
                }
            }

            for (final ChildItem part : new ArrayList<>(item.getChildItems())) {
                final Item nestedItem = part.getItem();
                if (nestedItem.getId().equals(idToRemove)) {
//                    part.setStatus("deleted");
                    item.getChildItems().remove(part);
                    processPartAction(ActionType.DELETE, part, item, user, itemService);
                }
            }

            itemService.update(item);
        }

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
