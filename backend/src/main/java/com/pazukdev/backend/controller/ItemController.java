package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@CrossOrigin
@Api(tags = "Item Controller", value = "API methods for items")
public class ItemController {

    private final ItemService service;

    @PutMapping("/file-upload/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Upload file")
    public boolean uploadFile(@PathVariable(name = "item-id") final Long itemId,
                              @RequestBody MultipartFile uploadedFile) throws IOException {

        final String defaultPath = "frontend/src/assets/";
        final Item item = service.getOne(itemId);
        final String itemCategory = item.getCategory();
        final String fileFolder = itemCategory.replaceAll(" ", "-").toLowerCase() + "/";
        final File directory = new File(defaultPath + fileFolder);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName;
        if (item.getImage() != null) {
            fileName = item.getImage();
        } else {
            final String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
            fileName = itemCategory.replaceAll(" ", "_").toLowerCase()
                    + "_"
                    + item.getName().replaceAll(" ", "_").toLowerCase()
                    + "." + extension;
            item.setImage(fileName);
            service.getRepository().save(item);
        }
        final String filePath = defaultPath + fileFolder + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(uploadedFile.getBytes());
        }
        return true;
    }

    @GetMapping("/get-view/{id}/{userName}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get item")
    public ItemView getView(@PathVariable final Long id, @PathVariable final String userName)  {
        return service.createItemView(id, userName);
    }

    @PostMapping("/create-view/{category}/{name}/{userName}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new item")
    public ItemView createView(@PathVariable final String category,
                               @PathVariable final String name,
                               @PathVariable String userName) {
        return service.createNewItemView(category, name, userName);
    }

    @PutMapping("/update-view/{id}/{userName}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update item")
    public ItemView updateView(@PathVariable final Long id,
                               @PathVariable String userName,
                               @RequestBody final ItemView itemView) {
        return service.updateItemView(id, userName, itemView);
    }

}
