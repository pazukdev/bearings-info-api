package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.DictionaryData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Api(tags = "File Controller", value = "API methods for files")
@CrossOrigin
public class FileController {

    @GetMapping(value = "/dictionary-data/{lang}")
    @ApiOperation(value = "Get dictionary data: 1. dictionary according to specified language; 2. available languages")
    public DictionaryData getDictionary(@PathVariable final String lang) throws Exception {
        return DictionaryData.getDictionaryFromFile(lang);
    }

}
