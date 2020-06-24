package com.pazukdev.backend.controller;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.PossibleNestedItemsDto;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.dto.factory.ItemViewFactory;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;

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
        return service.createItemView(id, userName, lang, option);
    }

    @GetMapping("/view/all-item-views/{number}/{userName}/{lang}")
    @ApiOperation(value = "Get all items views")
    public List<ItemView> getAllViews(@PathVariable final Integer number,
                                      @PathVariable String userName,
                                      @PathVariable String lang)  {
        final UserService userService = service.getUserService();
        if (isEmpty(userName) || userName.equals("undefined")) {
            userName = Role.GUEST.name().toLowerCase();
        }
        final UserEntity user = userService.findFirstByName(userName);
        final Set<Long> ids = ChildItemUtil.collectIds(user.getWishList().getItems());
        final UserDto userDto = UserConverter.convert(user);

        final List<Item> allItems = service.findAllActive();
        final ItemViewFactory factory = service.createNewItemViewFactory();
        final List<ItemView> views = new ArrayList<>();
        allItems.subList(0, number).forEach(item -> {
            final List<Item> copy = new ArrayList<>(allItems);
            copy.remove(item);
            views.add(factory.createItemViewForCache(
                    item,
                    copy,
                    FileUtil.getComments(),
                    userDto,
                    ids,
                    lang,
                    userService));
        });
        return views;
    }

    @GetMapping("/view/items/{status}/{userName}/{lang}")
    @ApiOperation(value = "Get items items list view")
    public ItemView getItemsListView(@PathVariable(name = "status") final String itemsStatus,
                           @PathVariable final String userName,
                           @PathVariable final String lang) {
        return service.createItemsListView(itemsStatus, userName, lang);
    }

    @GetMapping("/view/home/{userName}/{lang}")
    @ApiOperation(value = "Get home view")
    public ItemView getHomeView(@PathVariable final String userName, @PathVariable final String lang) {
        return service.createHomeView(userName, lang);
    }

    @PostMapping("/create/{category}/{name}/{userName}/{lang}")
    @ApiOperation(value = "Create new item")
    public ItemView create(@PathVariable final String category,
                           @PathVariable final String name,
                           @PathVariable final String userName,
                           @PathVariable final String lang) {
        return service.createNewItemView(category, name, userName, lang);
    }

    @PutMapping("/update/{id}/{userName}/{lang}")
    @ApiOperation(value = "Update item")
    public ItemView update(@PathVariable final String id,
                           @PathVariable final String userName,
                           @PathVariable final String lang,
                           @RequestBody final ItemView itemView) {
        return service.updateItemView(id, userName, lang, itemView);
    }

    @GetMapping("/edit-data/{itemId}")
    @ApiOperation(value = "Get edit data")
    public PossibleNestedItemsDto getEditData(@PathVariable final Long itemId) {
        return service.getEditData(itemId);
    }

}
