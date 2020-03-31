package com.pazukdev.backend.controller;

import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.factory.ItemViewFactory;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.FileUtil;
import com.pazukdev.backend.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user")
    public UserDto get(@PathVariable("id") final Long id) {
        return userConverter.convertToDto(userService.findOne(id));
    }

    @GetMapping("/view/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get public user data")
    public UserView getUserView(@PathVariable("id") final Long id) {
        return userConverter.convertToUserView(userService.findOne(id));
    }

    @GetMapping("/view/user/list/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get users list view")
    public ItemView getUserListView(@PathVariable final String userName, @PathVariable final String language) {
        final ItemViewFactory factory = new ItemViewFactory(itemService, FileUtil.getInfoCategories());
        return factory.createUserListView(userName, language);
    }

    @GetMapping("/user/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all users")
    public List<UserDto> getAll() {
        return userConverter.convertToDtoList(userService.findAll());
    }

    @GetMapping(value = "users/download/csv")
    @ApiOperation(value = "Download all users data as csv file")
    public void downloadUsersCsv(final HttpServletResponse response) throws IOException {
        IOUtils.copy(UserUtil.toCSVInputStream(userService), response.getOutputStream());
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=users");
        response.flushBuffer();
    }

    @PostMapping(value = "users/upload/{recoverUserData}/{recoverUserActions}")
    @ApiOperation(value = "Upload default users from file")
    public void uploadUsers(@PathVariable final boolean recoverUserData,
                            @PathVariable final boolean recoverUserActions) {
        final List<UserEntity> users = userService.getUsersFromRecoveryFile(recoverUserData);
        if (recoverUserActions) {
            userService.recoverUserActions(users, itemService);
        }
        userService.save(users);
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
        return userConverter.convertToDto(userService.findFirstByName(userName));
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
        userService.softDelete(id);
    }

    @DeleteMapping("/user/{id}/hard-delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete user")
    public void hardDelete(@PathVariable("id") final Long id) {
        userService.hardDelete(id);
    }

    @GetMapping("/admin/user/roles")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user roles")
    public Set<String> getRoles() {
        return userService.getRoles();
    }

    @PutMapping("user/{username}/add-item-to-wishlist/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Add item to wish list")
    public boolean addItemToWishList(@PathVariable("item-id") final Long id,
                                     @PathVariable("username") final String userName) {
        final Item item = itemService.findOne(id);
        return userService.addItemToWishList(item, userName);
    }

}
