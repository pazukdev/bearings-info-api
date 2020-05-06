package com.pazukdev.backend.controller;

import com.pazukdev.backend.dataloader.DataLoader;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/data-loader")
@RequiredArgsConstructor
@Api(tags = "API methods for data transfer between Google Spreadsheets and DB")
@CrossOrigin
public class DataLoaderController {

    private final DataLoader dataLoader;

    @PutMapping("/update/{itemCategory}/{itemName}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update item using data from Google Sheets table")
    public void update(@PathVariable final String itemCategory,
                       @PathVariable final String itemName) {
        dataLoader.updateItem(itemCategory, itemName);
    }

    @PutMapping("/update-all/{initialDBPopulation}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update all items using Google Sheets")
    public void updateAll(
            @ApiParam(defaultValue = "false", required = true)
            @PathVariable final boolean initialDBPopulation) {
        dataLoader.populate(initialDBPopulation);
    }

    @GetMapping(value = "users/download/csv")
    @ApiOperation(value = "Download all users data as csv file")
    public void downloadUsersCsv(final HttpServletResponse response) throws IOException {
        IOUtils.copy(UserUtil.toCSVInputStream(dataLoader.getItemService()), response.getOutputStream());
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=users");
        response.flushBuffer();
    }

    @PostMapping(value = "users/upload/{recoverUserData}/{recoverUserActions}")
    @ApiOperation(value = "Upload default users from file")
    public void uploadUsers(@PathVariable final boolean recoverUserData,
                            @PathVariable final boolean recoverUserActions) {
        final UserService userService = dataLoader.getItemService().getUserService();
        final List<UserEntity> users = userService.getUsersFromRecoveryFile(recoverUserData);
        if (recoverUserActions) {
            userService.recoverUserActions(users, dataLoader.getItemService());
        }
        userService.save(users);
    }

}
