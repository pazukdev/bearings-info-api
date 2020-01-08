package com.pazukdev.backend.controller;

import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.factory.ItemViewFactory;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.WishList;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "User Controller", value = "API methods for Users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final UserConverter userConverter;

    @GetMapping("/admin/user/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all Users. Admins-only permitted")
    public List<UserDto> getAll() {
        return userConverter.convertToDtoList(userService.findAll());
    }

    @GetMapping("/admin/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get User. Admins-only permitted")
    public UserDto get(@PathVariable("id") final Long id) {
        return userConverter.convertToDto(userService.getOne(id));
    }

    @GetMapping("/view/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get public user data")
    public UserView getUserView(@PathVariable("id") final Long id) {
        return userConverter.convertToUserView(userService.getOne(id));
    }

    @GetMapping("/view/user/list/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user list view")
    public ItemView getUserListView(@PathVariable final String userName, @PathVariable final String language) {
        return new ItemViewFactory(itemService).createUserListView(userName, language);
    }

    @GetMapping("/view/wishlist/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user wishlist view")
    public ItemView getWishlistView(@PathVariable final String userName, @PathVariable final String language) {
        return itemService.createWishlistView(userName, language);
    }

    @GetMapping(value = "user/find-by-name/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user by username")
    public UserDto searchByName(@PathVariable(name = "username") final String userName) {
        return userConverter.convertToDto(userService.findByName(userName));
    }

    @PostMapping("/user/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new User")
    public List<String> create(@RequestBody final UserDto dto) throws EntityExistsException, JSONException {
        return userService.createUser(dto);
    }

    @PutMapping("/user/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update user")
    public List<String> update(@PathVariable final Long id, @RequestBody final UserView userView) {
        return userService.updateUser(userView);
    }

    @DeleteMapping("/user/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete user")
    public void delete(@PathVariable("id") final Long id) {
        userService.delete(id);
    }

    @DeleteMapping("/admin/user/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete all users by ids list")
    public List<UserDto> delete(@RequestBody final List<Long> ids) {
        return userConverter.convertToDtoList(userService.deleteAll(ids));
    }

    @GetMapping("/admin/user/roles")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user roles")
    public Set<String> getRoles() {
        return userService.getRoles();
    }

    @GetMapping(value = "{userName}/wishlist/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user wishlist")
    public boolean isItemInWishList(@PathVariable final String userName, @PathVariable final Long itemId) {
        final Item item = itemService.getOne(itemId);
        final WishList wishList = userService.findByName(userName).getWishList();
        return wishList.getItems().contains(item);
    }

    @PutMapping("user/{username}/add-item-to-wishlist/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Add item to wish list")
    public boolean addItemToWishList(@PathVariable("item-id") final Long id,
                                     @PathVariable("username") final String userName) {
        final Item item = itemService.getOne(id);
        return userService.addItemToWishList(item, userName);
    }

}
