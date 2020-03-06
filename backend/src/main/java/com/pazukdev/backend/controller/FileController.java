package com.pazukdev.backend.controller;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.dto.DictionaryData;
import com.pazukdev.backend.dto.Message;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.UserActionUtil;
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
import java.util.List;

import static com.pazukdev.backend.util.FileUtil.*;
import static com.pazukdev.backend.util.TranslatorUtil.addLang;
import static com.pazukdev.backend.util.TranslatorUtil.translate;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Api(tags = "File Controller", value = "API methods for files upload / download")
@CrossOrigin
public class FileController {

    private final UserService userService;
    private final UserActionRepository userActionRepository;

    @GetMapping(value = "/dictionary-data/{lang}")
    @ApiOperation(value = "Get dictionary data: 1. dictionary according to specified language; 2. available languages")
    public DictionaryData getDictionary(@PathVariable final String lang) throws Exception {
        return DictionaryData.getDictionaryFromFile(lang);
    }

    @GetMapping(value = "/{fileName}/download")
    @ApiOperation(value = "File download")
    public void download(@PathVariable final String fileName,
                         final HttpServletResponse response) throws IOException {


        final InputStream inputStream = Files.newInputStream(getTxtFilePath(fileName));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + FileFormat.TXT);
        response.flushBuffer();
    }

    @PutMapping(value = "/{fileName}/upload/{username}/{lang}")
    @ApiOperation(value = "File upload")
    public Message upload(@PathVariable final String fileName,
                          @PathVariable final String username,
                          @PathVariable final String lang,
                          @RequestBody final Message request) {

        final Message response = new Message();
        DictionaryData oldDictionary = null;

        try {
            oldDictionary = DictionaryData.getDictionaryFromFile(lang);

            if (fileName == null) {
                response.setText("File name is null");
                response.translate(lang, oldDictionary.getDictionary());
                return response;
            }

            if (fileName.equals(FileName.DICTIONARY)) {
                return uploadDictionaryFile(username, lang, request, response, oldDictionary);
            }

            createFileInFileSystem(fileName, request.getText().getBytes(StandardCharsets.UTF_8));

            response.setText("New " + fileName + " file accepted");
            response.translate(lang, oldDictionary.getDictionary());
        } catch (final Exception e) {
            final String eMessage = e.getMessage();

            response.setText(eMessage);
            response.setLocalizedText(eMessage);

            if (oldDictionary != null) {
                final List<String> dictionary = oldDictionary.getDictionary();
                response.setLocalizedText(translate("en", lang, response.getText(), false, false, dictionary));
            }
        }

        return response;
    }

    private Message uploadDictionaryFile(final String username,
                                         final String oldDictionaryLang,
                                         final Message request,
                                         final Message response,
                                         final DictionaryData oldDictionary) throws Exception {
        final UserEntity user = userService.findFirstByName(username);

        final String text = request.getText();
        DictionaryData newDictionary;
        try {
            newDictionary = DictionaryData.createDictionary(text);
        } catch (final Exception e) {
            response.setText(e.getMessage());
            response.translate(oldDictionaryLang, oldDictionary.getDictionary());
            return response;
        }
        final List<String> newDictRows = newDictionary.getDictionary();
        final String newDictionaryLang = newDictionary.getLang();

        final String acceptedMessage = "New dictionary accepted";
        final boolean isNewLang = !getTxtFileTextLines("langs").contains(newDictionaryLang);
        if (isNewLang) {
            DictionaryData.saveDictionary(newDictionary);
            addLang(newDictionaryLang);
            final String toTranslate = acceptedMessage + ". New language added";
            response.setText(toTranslate);
            response.translate(oldDictionaryLang, oldDictionary.getDictionary());
            response.setText(response.getText() + ": " + newDictionaryLang);
            response.setLocalizedText(response.getLocalizedText() + ": " + newDictionaryLang);
            return response;
        }

        final int newDictionarySize = newDictionary.getDictionary().size();
        final int oldDictionarySize = oldDictionary.getDictionary().size();

        final int difference = oldDictionarySize - newDictionarySize;
        final int removedLinesLimit = user.getRole() == Role.ADMIN ? 5 : 1;

        if (difference > removedLinesLimit) {
            final String responseText = "Dictionary is not accepted. " +
                    "New size is " + newDictionarySize + " lines. " +
                    difference + " lines were removed. " +
                    "You can't remove more than " + removedLinesLimit + " line at a time";
            response.setText(responseText);
            response.translate(oldDictionaryLang, oldDictionary.getDictionary());
            return response;
        }

        DictionaryData.saveDictionary(newDictionary);

        final String plus = -difference > 0 ? "+" : "";
        String changed = ": " + plus + (-difference) + " lines";

        UserActionUtil.processUploadDictionaryAction("upload dictionary", changed, user, userActionRepository);

        if (difference == 0) {
            changed = "";
        }

        response.setText(acceptedMessage + changed);
        response.translate(newDictionaryLang, newDictRows);
        return response;

    }

}
