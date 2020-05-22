package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.converter.NestedItemConverter;
import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.service.EmailSenderService;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.*;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.pazukdev.backend.converter.NestedItemConverter.convert;
import static com.pazukdev.backend.dto.DictionaryData.getDictionaryFromFile;
import static com.pazukdev.backend.util.CategoryUtil.Category;
import static com.pazukdev.backend.util.CategoryUtil.Category.VEHICLE;
import static com.pazukdev.backend.util.CategoryUtil.Parameter;
import static com.pazukdev.backend.util.ChildItemUtil.collectIds;
import static com.pazukdev.backend.util.ItemUtil.SpecialItemId.*;
import static com.pazukdev.backend.util.ItemUtil.*;
import static com.pazukdev.backend.util.LinkUtil.setLinksToItemView;
import static com.pazukdev.backend.util.LinkUtil.updateLinks;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.TableUtil.createHeader;
import static com.pazukdev.backend.util.TableUtil.createReplacersTable;
import static com.pazukdev.backend.util.TranslatorUtil.translate;
import static com.pazukdev.backend.util.UserActionUtil.*;
import static com.pazukdev.backend.validator.CodeValidator.isLangCodeValid;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public class ItemViewFactory {

    private final ItemService itemService;
    private final List<String> infoCategories;
    private final EmailSenderService emailSenderService;

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
        basicView.setUserData(UserConverter.convert(currentUser));

        ItemView view;

        boolean userListView = itemId.equals(USER_LIST_VIEW.getItemId());

        if (itemId.equals(WISH_LIST_VIEW.getItemId())) {
            view = createWishListView(basicView, currentUser, userService);
        } else if (itemId.equals(VEHICLES_VIEW.getItemId())) {
            view = createVehiclesView(basicView, userService);
        } else if (itemId.equals(ITEMS_MANAGEMENT_VIEW.getItemId())) {
            view = createItemsListView(basicView, status);
        } else if (userListView) {
            view = createUsersListView(basicView, userService);
        } else {
            view = createOrdinaryItemView(basicView, itemId, userService);
        }

        final double businessLogicEndTime = System.nanoTime();
        final double businessLogicDuration = businessLogicEndTime - businessLogicStartTime;

        if (!userLang.equals("en") && !userListView && isLangCodeValid(userLang)) {
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

        if (containsEmpty(category, name, userName, userLanguage)) {
            throw new Exception("category, name, userName or userLanguage is empty");
        }

        final UserEntity creator = itemService.getUserService().findFirstByName(userName);
        final Item item = createNewItem(name.trim(), category.trim(), creator, userLanguage);

        LoggerUtil.warn(
                UserActionUtil.createAction(ActionType.CREATE, "", null, item, creator, false),
                itemService.getUserActionRepo(),
                item,
                creator,
                itemService.getEmailSenderService());

        final ItemView view = createItemView(item.getId(), Status.ACTIVE, userName, userLanguage);
        view.setNewItem(true);

        setTime (view, (double) (System.nanoTime() - businessLogicStartTime), null);
        return view;
    }

    private Item createNewItem(String name,
                               String category,
                               final UserEntity creator,
                               final String lang) throws Exception {

        if (!lang.equals("en") && isLangCodeValid(lang)) {
            final DictionaryData dictionaryData = getDictionaryFromFile(lang);
            final List<String> dictionary = dictionaryData.getDictionary();
            name = translate(lang, "en", name, true, false, dictionary);
            category = translate(lang, "en", category, false, true, dictionary);
        }

        final Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setImg("-");
        item.setCreatorId(creator.getId());
        item.setUserActionDate(DateUtil.now());
        item.setDescription(createEmptyDescription(category, itemService.getItemRepository()));
        itemService.update(item);
        UserActionUtil.createAction(ActionType.CREATE, "", null, item, creator, false);
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
                                            final UserService userService) {
        final Item item = itemService.findOne(itemId);
        final List<Item> allItems = itemService.findAllActive();
        allItems.remove(item);
        final String category = item.getCategory();
        final String name = item.getName();
        final ImgViewData imgViewData = ImgUtil.getImg(item);
        final Map<String, String> description = toMap(item.getDescription());

        view.setSearchEnabled(true);
        view.setOrdinaryItem(true);
        view.setCategory(category);
        view.setManufacturer(description.get(Category.MANUFACTURER));
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
        view.setCreatorData(UserUtil.getCreator(item, itemService.getUserService()));
        setLinksToItemView(view, item);
        view.setParents(createParentItemsView(item, userService, allItems));
        return view;
    }

    private static List<NestedItemDto> createChildren(final Item item,
                                                      final UserService service,
                                                      final boolean all) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        final Set<NestedItem> parts = item.getParts();
        addParts(parts, dtos, service, all, null);
        return dtos;
    }

    public static void addParts(final Set<NestedItem> parts,
                                final List<NestedItemDto> dtos,
                                final UserService userService,
                                final boolean summary,
                                final Double parentQuantity) {
        for (final NestedItem part : parts) {
            boolean add = true;
            final NestedItemDto partDto = NestedItemConverter.convert(part, userService, !summary);
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
                addParts(part.getItem().getParts(), dtos, userService, true, quantity);
            }
        }
    }

    private ItemView createVehiclesView(final ItemView view, final UserService userService) {
        final List<Item> vehicles = itemService.find(VEHICLE);

        final List<NestedItemDto> dtos = new ArrayList<>();
        vehicles.forEach(vehicle -> dtos.add(NestedItemDto.createVehicle(vehicle, userService)));

        view.setChildren(dtos);
        view.setAdminMessage(AdminMessage.getMessage(itemService.getAdminMessageRepo()));
        view.setLastVehicles(getLastNewVehicles(itemService));
        view.setLastReplacers(getLastNewReplacers(itemService));
        return view;
    }

    private ItemView createItemsListView(final ItemView view, final String itemsStatus) {
        final List<Item> items = itemService.findAll(itemsStatus);
        final List<String> comments = FileUtil.getComments();

        final List<NestedItemDto> dtos = new ArrayList<>();
        items.forEach(item -> dtos.add(NestedItemDto.createItemForItemsManagement(item, itemService.getUserService(), comments)));

        view.setChildren(dtos);
        view.setAllCategories(new ArrayList<>(itemService.collectCategories(items)));
        return view;
    }

    private ItemView createParentItemsView(final Item item,
                                           final UserService userService,
                                           final List<Item> allItems) {
        final List<String> comments = FileUtil.getComments();

        final ItemView view = new ItemView();
        final List<NestedItemDto> dtos = new ArrayList<>();
        itemService.findParents(item, allItems, infoCategories)
                .forEach(parent -> dtos.add(NestedItemDto.createItemForItemsManagement(parent, userService, comments)));
        view.setChildren(dtos);
        return view;
    }

    private ItemView createUsersListView(final ItemView view, final UserService userService) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        userService.findAll().forEach(user -> dtos.add(NestedItemConverter.convert(user)));
        view.setChildren(dtos);
        return view;
    }

    private ItemView createWishListView(final ItemView view,
                                        final UserEntity user,
                                        final UserService service) {
        final List<NestedItemDto> dtos = new ArrayList<>();
        for (final NestedItem item : user.getWishList().getItems()) {
            final Item child = item.getItem();
            final String comment = item.getComment();
            final String quantity = item.getQuantity();
            dtos.add(NestedItemDto.createWishListItem(child, comment, quantity, user.getId(), service));
        }
        view.setChildren(dtos);
        return view;
    }

    private ItemView updateItem(final Long itemId,
                                final ItemView view,
                                final UserEntity currentUser,
                                final String userLang) {

        final long businessLogicStartTime = System.nanoTime();

        final UserActionRepository userActionRepo = itemService.getUserActionRepo();

        final long translationFromUserLang = System.nanoTime();
        if (!userLang.equals("en") && isLangCodeValid(userLang)) {
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
        final String newStatus = view.getStatus();

        final Item item = itemService.findOne(itemId);

        final Item oldItemCopy = new Item();
        oldItemCopy.setId(item.getId());
        oldItemCopy.setName(item.getName());
        oldItemCopy.setCategory(item.getCategory());

        final List<Item> allItems = itemService.findAllActive();
        allItems.remove(item);

        final List<UserAction> actions = new ArrayList<>();
        createActions(header, item, newStatus, actions, currentUser);

        header.removeRow(Parameter.DescriptionIgnored.NAME);
        header.removeRow(Parameter.DescriptionIgnored.CATEGORY);

        final Map<String, String> newDescriptionMap = TableUtil.createHeaderMap(header);
        final String newDescription = toDescription(newDescriptionMap);

        boolean moveItemToAnotherCategory = updateNameAndCategory(item, newCategory, newName, allItems, infoCategories, itemService);

        if (moveItemToAnotherCategory) {
            moveItemToAnotherCategory(item, newCategory, newDescriptionMap, itemService);
        } else if (!item.getDescription().equals(newDescription)) {
            item.setDescription(newDescription);
            applyNewDescriptionToCategory(newCategory, header, newDescriptionMap, allItems, itemService);
        }

        updateNestedItems(item, view, currentUser, itemService, actions);
        updateLinks(item, view, currentUser, actions);
        item.setStatus(newStatus);

        itemService.update(item);

        final ItemView newItemView = createItemView(itemId, item.getStatus(), currentUser.getName(), userLang);


        LoggerUtil.warn(actions, userActionRepo, oldItemCopy, currentUser, emailSenderService);

        final double totalTranslationTime = view.getTranslationTime() * 1000000000 + translationFromUserLangDuration;
        setTime(newItemView, (double) (System.nanoTime() - businessLogicStartTime), totalTranslationTime);
        return newItemView;
    }

    private ItemView editWishList(final ItemView view, final UserEntity user) {
        final Long id = view.getItemId();
        final Set<NestedItem> newWishListItems = convert(view.getChildren(), id, itemService, user);
        final Set<NestedItem> toRemove = new HashSet<>();
        for (final NestedItem oldWishListItem : user.getWishList().getItems()) {
            boolean remove = true;
            for (final NestedItem newWishListItem : newWishListItems) {
                final Long newItemId = newWishListItem.getItem().getId();
                final Long oldItemId = oldWishListItem.getItem().getId();
                final boolean updateOldWishListItem = oldItemId.equals(newItemId);
                if (updateOldWishListItem) {
                    oldWishListItem.setComment(replaceEmptyWithDash(newWishListItem.getComment()));
                    oldWishListItem.setQuantity(replaceEmptyWithDash(newWishListItem.getQuantity()));
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
        final List<UserAction> actions = new ArrayList<>();
        for (final Long idToRemove : idsToRemove) {
            final Item itemToRemove = itemService.findOne(idToRemove);
            removeItemFromAllWishLists(itemToRemove, userService);
            removeItemFromAllParentItems(idToRemove, currentUser, actions);
            removeItem(itemToRemove, currentUser, actions);
        }
        LoggerUtil.warn(
                actions,
                itemService.getUserActionRepo(),
                null,
                currentUser,
                itemService.getEmailSenderService());
    }

    private void removeItem(final Item itemToRemove,
                            final UserEntity user,
                            final List<UserAction> actions) {
        final String status = itemToRemove.getStatus();
        if (status.equals(Status.DELETED)) {
            itemService.hardDelete(itemToRemove.getId());
            return;
        }
        itemToRemove.setStatus(Status.DELETED);
        itemToRemove.setUserActionDate(DateUtil.now());
        itemService.update(itemToRemove);
        actions.add(createAction(ActionType.DELETE, "", null, itemToRemove, user, false));
    }

    private void removeItemFromAllWishLists(final Item itemToRemove, final UserService userService) {
        for (final UserEntity user : userService.findAll()) {
            user.getWishList().getItems()
                    .removeIf(wishListItem -> wishListItem.getItem().getId().equals(itemToRemove.getId()));
            userService.update(user);
        }
    }

    private void removeItemFromAllParentItems(final Long idToRemove,
                                              final UserEntity user,
                                              final List<UserAction> actions) {
        for (final Item item : itemService.findAllActive()) {
            final List<NestedItem> nestedItems = new ArrayList<>(item.getReplacers());
            nestedItems.addAll(new ArrayList<>(item.getParts()));

            for (final NestedItem nestedItem : nestedItems) {
                final Item child = nestedItem.getItem();
                if (child.getId().equals(idToRemove)) {
                    if (nestedItem.getType().equals(NestedItem.Type.REPLACER.toString().toLowerCase())) {
                        item.getReplacers().remove(nestedItem);
                    } else {
                        item.getParts().remove(nestedItem);
                    }
                    actions.add(createAction(ActionType.DELETE, "", item, nestedItem, user, true));
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
