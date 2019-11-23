package com.pazukdev.backend.controller;

import com.pazukdev.backend.dto.Message;
import com.pazukdev.backend.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Api(tags = "File Controller", value = "API methods for files upload / download")
@CrossOrigin
public class FileController {

    @GetMapping(value = "/dictionary/download")
    @ApiOperation(value = "Download dictionary file which contains translations for app texts")
    public void downloadDictionaryFile(final HttpServletResponse response) throws IOException {
        final InputStream inputStream = Files.newInputStream(FileUtil.getDictionaryFilePath());
        IOUtils.copy(inputStream, response.getOutputStream());
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=dictionary.txt");
        response.flushBuffer();
    }

    @PutMapping(value = "/dictionary/upload")
    @ApiOperation(value = "Upload new translations for the app")
    public void uploadDictionaryFile(@RequestBody final Message message) throws IOException {
        FileUtil.createDictionaryFileInFileSystem(message.getText().getBytes(StandardCharsets.UTF_8));
    }

}
