package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.RateReplacer;
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
@RequestMapping("/replacer")
@RequiredArgsConstructor
@Api(tags = "API methods for item replacers")
@CrossOrigin
public class ReplacerController {

    private final ItemService itemService;

    @PutMapping("/rate/{userName}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Rate replacer")
    public RateReplacer rateReplacer(@PathVariable final String userName, @RequestBody final RateReplacer rate) {
        return itemService.rateReplacer(userName, rate);
    }

}
