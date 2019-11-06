package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping("/file-upload/{item-id}/{imageData}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Upload file")
    public boolean uploadFile(@PathVariable(name = "item-id") final Long itemId,
                              @PathVariable String imageData) throws IOException {

//        final String extension = "png";
//        final String defaultPath = "backend/src/img/";
//        final Item item = service.getOne(itemId);
//        final String itemCategory = item.getCategory();
//        final String fileFolder = itemCategory.replaceAll(" ", "-").toLowerCase() + "/";
//        final File directory = new File(defaultPath + fileFolder);
//        if (!directory.exists()) {
//            directory.mkdir();
//        }
//        String fileName = "";
//        if (item.getImage() != null) {
//            fileName = item.getImage();
//        } else {
//            fileName = itemCategory.replaceAll(" ", "_").toLowerCase()
//                    + "_"
//                    + item.getName().replaceAll(" ", "_").toLowerCase()
//                    + "." + extension;
//            item.setImage(fileName);
//            service.getRepository().save(item);
//        }
//        final String filePath = defaultPath + fileFolder + fileName;
//        URL url = new URL("https://www.google.by/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png");
//        BufferedImage img = ImageIO.read(url);
//        File file = new File(filePath);
//        ImageIO.write(img, extension, file);
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
                               @RequestBody final ItemView itemView) throws IOException {
        return service.updateItemView(id, userName, itemView);
    }

}
