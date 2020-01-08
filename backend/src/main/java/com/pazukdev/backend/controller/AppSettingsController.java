package com.pazukdev.backend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/app-settings")
@RequiredArgsConstructor
@Api(tags = "App Settings Controller", value = "API methods for getting general app settings data")
@CrossOrigin
public class AppSettingsController {

    @GetMapping("/langs")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of supported languages")
    public List<String> getAppLanguages()  {
        return new ArrayList<>(Arrays.asList("en", "ru", "all"));
    }

}
