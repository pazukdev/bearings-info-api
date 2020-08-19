package com.pazukdev.backend.controller;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.dto.PossibleNestedItemsDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import static com.pazukdev.backend.util.ItemUtil.SpecialItemId.*;
import static com.pazukdev.backend.util.TranslatorUtil.translate;
import static com.pazukdev.backend.validator.CodeValidator.isLangCodeValid;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@Api(tags = "API methods for items")
@CrossOrigin
public class ItemController {

    private final ItemService service;

    @GetMapping("/view/item/{id}/{userName}/{lang}/{option}")
    @ApiOperation(value = "Get item by id")
    public ItemView get(@PathVariable final String id,
                        @PathVariable final String userName,
                        @PathVariable final String lang,
                        @PathVariable final String option)  {
        return getView(id, userName, lang, option, Status.ACTIVE);
//        return service.createItemView(id, userName, lang, option);
    }

//    @GetMapping("/view/all-item-views/{number}/{userName}/{lang}")
//    @ApiOperation(value = "Get all items views")
//    public List<ItemView> getAllViews(@PathVariable final Integer number,
//                                      @PathVariable String userName,
//                                      @PathVariable String lang)  {
//        return service.getAllViews(number, userName, lang);
//    }

    @GetMapping("/view/items/{status}/{userName}/{lang}")
    @ApiOperation(value = "Get items items list view")
    public ItemView getItemsListView(@PathVariable(name = "status") final String itemsStatus,
                                     @PathVariable final String userName,
                                     @PathVariable final String lang) {
        return getView(ITEMS_MANAGEMENT_VIEW.name(), userName, lang, null, itemsStatus);
    }

    @GetMapping("/view/home/{userName}/{lang}")
    @ApiOperation(value = "Get home view")
    public ItemView getHomeView(@PathVariable final String userName,
                                @PathVariable final String lang) {
        return getView(VEHICLES_VIEW.name(), userName, lang, null, Status.ACTIVE);
    }

    @PostMapping("/create/{category}/{name}/{userName}/{lang}")
    @ApiOperation(value = "Create new item")
    public ItemView create(@PathVariable final String category,
                           @PathVariable final String name,
                           @PathVariable final String userName,
                           @PathVariable final String lang) {
        final ItemView view = service.createNewItemView(category, name, userName, lang);
        service.getCachedViews().clear();
//        service.putCachedView(view, lang);
        return view;
    }

    @PutMapping("/update/{id}/{userName}/{lang}")
    @ApiOperation(value = "Update item")
    public ItemView update(@PathVariable final String id,
                           @PathVariable final String userName,
                           @PathVariable final String lang,
                           @RequestBody final ItemView itemView) {
        final ItemView view = service.updateItemView(id, userName, lang, itemView);
        service.getCachedViews().clear();
//        service.putCachedView(view, lang);
        return view;
    }

    @GetMapping("/edit-data/{itemId}")
    @ApiOperation(value = "Get edit data")
    public PossibleNestedItemsDto getEditData(@PathVariable final Long itemId) {
        return service.getEditData(itemId);
    }

    private ItemView getView(final String id,
                             final String userName,
                             final String lang,
                             final String option,
                             final String status) {
        final long businessLogicStartTime = System.nanoTime();
        ItemView view = service.getCachedView(id, lang);
        boolean cached = true;
        if (view == null) {
            cached = false;
            if (id.equals(VEHICLES_VIEW.name())) {
                view = service.createHomeView(userName, null, "en");
            } else if (id.equals(ITEMS_MANAGEMENT_VIEW.name())) {
                view = service.createItemsListView(status, userName, null, "en");
            } else {
                view = service.createItemView(id, userName, null, lang, option);
            }
        }
        boolean userListView = id.equals(USER_LIST_VIEW.name());

        final double businessLogicEndTime = System.nanoTime();
        final double businessLogicDuration = businessLogicEndTime - businessLogicStartTime;

        final ItemView translatedView = SerializationUtils.clone(view);
        final UserEntity guest = UserUtil.getUser("guest", service.getUserService());
        UserUtil.setUserDataTo(view, guest);
        service.putCachedView(view, lang);

        if (!cached || translatedView.getLang().equals("en")) {
            if (!lang.equals("en") && !userListView && isLangCodeValid(lang)) {
                try {
                    translate("en", lang, translatedView, false);
                } catch (Exception e) {
                    translatedView.setErrorMessage(e.getMessage());
                }
            }
        }
        double translationDuration = System.nanoTime() - businessLogicEndTime;

        service.setTime(translatedView, businessLogicDuration, translationDuration);
//        service.putCachedView(view, lang);
        translatedView.setCachedViews(service.getCachedViews().size());
        setUserData(translatedView, userName);
        return translatedView;
    }

    public void setUserData(final ItemView view, final String userName) {
        final UserEntity currentUser = UserUtil.getUser(userName, service.getUserService());
        UserUtil.setUserDataTo(view, currentUser);
    }

}
