package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "API methods for files")
@CrossOrigin
public class FileController {

    @GetMapping(value = "/download/dictionary-data/{lang}")
    @ApiOperation(value = "Get dictionary data: 1. dictionary according to specified language; 2. available languages")
    public DictionaryData getDictionary(@PathVariable final String lang) throws Exception {
        return DictionaryData.getDictionaryFromFile(lang);
    }

    @GetMapping(value = "/download/comments")
    public List<String> getComments() {
        return FileUtil.getComments();
    }

    @GetMapping(value = "/download/info-categories")
    public List<String> getInfoCategories() {
        return FileUtil.getInfoCategories();
    }

    @GetMapping(value = "/download/items-data-google-sheets-ids/{fromGoogleDocs}")
    public List<String> getItemsDataGoogleSheetsIds(
            @ApiParam(defaultValue = "true", required = true)
            @PathVariable boolean fromGoogleDocs) {
        return Arrays.asList(FileUtil.getItemsDataGoogleSheetsIds(fromGoogleDocs));
    }

    @GetMapping(value = "/download/read-google-spreadsheet/{documentId}")
    public List<List<String>> readGoogleDocSpreadsheet(
            @ApiParam(defaultValue = "1SJcfsoOmpMA5auCEWprmfu5_5hyL_LQTPrT95fuKyvk", required = true)
            @PathVariable final String documentId) {
        return FileUtil.readGoogleDocSpreadsheet(documentId);
    }

    @GetMapping(value = "/download/{fileName}")
    @ApiOperation(value = "File download")
    public void downloadFile(@PathVariable final String fileName,
                             final HttpServletResponse rs) throws IOException {
        download(fileName, rs);
    }

    @GetMapping(value = "/loaderio-76fe54dfcb6f794f1145783b4d0b855b.txt")
    @ApiOperation(value = "Download loader.io url verification file")
    public void downloadLoaderVerificationFile(final HttpServletResponse rs) throws IOException {
        download("loaderio-76fe54dfcb6f794f1145783b4d0b855b.txt", rs);
    }

    private void download(final String fileName,
                          final HttpServletResponse rs) throws IOException {
        final InputStream is = Files.newInputStream(Paths.get("backend/src/download/" + fileName));
        IOUtils.copy(is, rs.getOutputStream());
        rs.setContentType("text/plain");
        rs.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        rs.flushBuffer();
    }

}
