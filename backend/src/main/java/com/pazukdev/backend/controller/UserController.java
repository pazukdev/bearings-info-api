package com.pazukdev.backend.controller;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.dto.factory.ItemViewFactory;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.EmailSenderService;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "API methods for Users")
@CrossOrigin
public class UserController {

    private final ItemService itemService;
    private final UserConverter userConverter;
    private final EmailSenderService emailSenderService;

    @GetMapping("/view/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get public user data")
    public UserView getUserView(@PathVariable("id") final Long id) {
        return userConverter.convertToUserView(itemService.getUserService().findOne(id));
    }

    @GetMapping("/view/user/list/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get users list view")
    public ItemView getUserListView(@PathVariable final String userName, @PathVariable final String language) {
        final ItemViewFactory factory = new ItemViewFactory(itemService, FileUtil.getInfoCategories(), emailSenderService);
        return factory.createUserListView(userName, language);
    }

    @GetMapping("/view/wishlist/{userName}/{language}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get user wishlist view")
    public ItemView getWishlistView(@PathVariable final String userName, @PathVariable final String language) {
        return itemService.createWishlistView(userName, language);
    }

    @PostMapping("/user/create/{lang}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new User")
    public List<String> create(@PathVariable final String lang, @RequestBody final UserDto dto) throws EntityExistsException, JSONException {
        return itemService.getUserService().createUser(dto, lang);
    }

    @PostMapping("/user/id/{id}/activate")
    @ApiOperation(value = "Create new User")
    public String activate(@PathVariable final Long id) throws Exception {
        final UserEntity user = itemService.getUserService().findFirst(id);
        if (user != null) {
            final String newStatus = Status.ACTIVE;
            user.setStatus(newStatus);
            itemService.getUserService().getRepository().save(user);
            return newStatus;
        } else {
            throw new Exception("user id=" + id + " not activated");
        }
    }

    @PutMapping("/user/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update user")
    public List<String> update(@PathVariable final Long id, @RequestBody final UserView userView) {
        return itemService.getUserService().updateUser(userView);
    }

    @DeleteMapping("/user/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete user")
    public void delete(@PathVariable("id") final Long id) {
        itemService.getUserService().softDelete(id);
    }

    @DeleteMapping("/user/{id}/hard-delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete user")
    public void hardDelete(@PathVariable("id") final Long id) {
        itemService.getUserService().hardDelete(id);
    }

    @PutMapping("user/{username}/add-item-to-wishlist/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Add item to wish list")
    public boolean addItemToWishList(@PathVariable("item-id") final Long id,
                                     @PathVariable("username") final String userName) {
        final Item item = itemService.findOne(id);
        return itemService.getUserService().addItemToWishList(item, userName);
    }

}
