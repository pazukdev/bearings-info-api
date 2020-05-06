package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Api(tags = "API methods for files")
@CrossOrigin
public class FileController {

    @GetMapping(value = "/dictionary-data/{lang}")
    @ApiOperation(value = "Get dictionary data: 1. dictionary according to specified language; 2. available languages")
    public DictionaryData getDictionary(@PathVariable final String lang) throws Exception {
        return DictionaryData.getDictionaryFromFile(lang);
    }

    @GetMapping(value = "/comments")
    public List<String> getComments() {
        return FileUtil.getComments();
    }

    @GetMapping(value = "/info-categories")
    public List<String> getInfoCategories() {
        return FileUtil.getInfoCategories();
    }

    @GetMapping(value = "/items-data-google-sheets-ids/{fromGoogleDocs}")
    public List<String> getItemsDataGoogleSheetsIds(
            @ApiParam(defaultValue = "true", required = true)
            @PathVariable boolean fromGoogleDocs) {
        return Arrays.asList(FileUtil.getItemsDataGoogleSheetsIds(fromGoogleDocs));
    }

    @GetMapping(value = "/read-google-spreadsheet/{documentId}")
    public List<List<String>> readGoogleDocSpreadsheet(
            @ApiParam(defaultValue = "1SJcfsoOmpMA5auCEWprmfu5_5hyL_LQTPrT95fuKyvk", required = true)
            @PathVariable final String documentId) {
        return FileUtil.readGoogleDocSpreadsheet(documentId);
    }

}
