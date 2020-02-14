package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.AdminMessage;
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
@RequestMapping("/info")
@RequiredArgsConstructor
@Api(tags = "Info Controller", value = "Information for users")
@CrossOrigin
public class InfoController {

    private final ItemService itemService;

    @PostMapping("/create-admin-message")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new message from admin")
    public void setAdminMessage(@RequestBody final AdminMessage adminMessage) {
        itemService.setAdminMessage(adminMessage);
    }

}
