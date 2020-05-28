package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.PossibleNestedItemsDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/view/items/{status}/{userName}/{lang}")
    @ApiOperation(value = "Get items items list view")
    public ItemView getAll(@PathVariable(name = "status") final String itemsStatus,
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
