package com.pazukdev.backend.controller;

import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.entity.WishList;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "User Controller", value = "API methods for Users")
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
    public UserDto get(@PathVariable("id") Long id) {
        return userConverter.convertToDto(userService.getOne(id));
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

    @PutMapping("/admin/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update user")
    public List<String> update(@PathVariable("id") final Long id, @RequestBody final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/admin/user/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete User. Admins-only permitted")
    public UserDto delete(@PathVariable("id") final Long id) {
        return userConverter.convertToDto(userService.delete(id));
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

    @PutMapping(value = "{userName}/add-item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Add item to wish list")
    public boolean addItem(@PathVariable final String userName, @PathVariable final Long itemId) {
        final UserEntity user = userService.findByName(userName);
        final WishList wishList = user.getWishList();
        final Item item = itemService.getOne(itemId);
        if (!isItemInWishList(userName, item)) {
            wishList.getItems().add(item);
            userService.update(user);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping(value = "{userName}/wishlist/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user wishlist")
    public boolean isItemInWishList(@PathVariable final String userName, @PathVariable final Long itemId) {
        return isItemInWishList(userName, itemService.getOne(itemId));
    }

    private boolean isItemInWishList(final String userName, final Item item) {
        final WishList wishList = userService.findByName(userName).getWishList();
        return wishList.getItems().contains(item);
    }

}
