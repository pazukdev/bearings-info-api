package com.pazukdev.backend.controller;

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
@Api(tags = "Item Controller", value = "API methods for items")
@CrossOrigin
public class ItemController {

    private final ItemService service;

    @GetMapping("/view/item/{id}/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get item")
    public ItemView get(@PathVariable final Long id,
                        @PathVariable final String userName,
                        @PathVariable final String language)  {
        return service.createItemView(id, userName, language);
    }

    @GetMapping("/view/items-management/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get items management view")
    public ItemView getAll(@PathVariable final String userName, @PathVariable final String language) {
        return service.createItemsManagementView(userName, language);
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
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update item")
    public ItemView update(@PathVariable final Long id,
                           @PathVariable String userName,
                           @PathVariable String language,
                           @RequestBody final ItemView itemView) {
        return service.updateItemView(id, userName, language, itemView);
    }

}
