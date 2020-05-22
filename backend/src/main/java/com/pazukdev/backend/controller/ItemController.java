package com.pazukdev.backend.controller;

import com.pazukdev.backend.dataloader.DataLoader;
import com.pazukdev.backend.dto.PossibleNestedItemsDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final DataLoader dataLoader;

    @GetMapping("/view/item/{id}/{userName}/{lang}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get item")
    public ItemView get(@PathVariable final Long id,
                        @PathVariable final String userName,
                        @PathVariable final String lang)  {
        return service.createItemView(id, userName, lang);
    }

    @GetMapping("/view/items/{status}/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get items items list view")
    public ItemView getAll(@PathVariable(name = "status") final String itemsStatus,
                           @PathVariable final String userName,
                           @PathVariable final String language) {
        return service.createItemsListView(itemsStatus, userName, language);
    }

    @GetMapping("/view/home/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get home view")
    public ItemView getHomeView(@PathVariable final String userName, @PathVariable final String language) {
        return service.createHomeView(userName, language);
    }

    @PostMapping("/create/{category}/{name}/{userName}/{language}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new item")
    public ItemView create(@PathVariable final String category,
                           @PathVariable final String name,
                           @PathVariable final String userName,
                           @PathVariable final String language) {
        return service.createNewItemView(category, name, userName, language);
    }

    @PutMapping("/update/{id}/{userName}/{language}")
    @ApiOperation(value = "Update item")
    public ItemView update(@PathVariable final Long id,
                           @PathVariable final String userName,
                           @PathVariable final String language,
                           @RequestBody final ItemView itemView) {
        return service.updateItemView(id, userName, language, itemView);
    }

    @GetMapping("/edit-data/{itemId}")
    @ApiOperation(value = "Get edit data")
    public PossibleNestedItemsDto getEditData(@PathVariable final Long itemId) {
        return service.getEditData(itemId);
    }

}
