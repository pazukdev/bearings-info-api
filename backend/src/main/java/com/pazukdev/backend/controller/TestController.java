package com.pazukdev.backend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Siarhei Sviarkaltsau
 */
@RestController
@Api(tags = "Test Controller", value = "Test methods")
@RequiredArgsConstructor
@CrossOrigin
public class TestController {

    @GetMapping("/test/public")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get test response from public url")
    public String getTestResponseFromPublicUrl() {
        return "This is test response from public url";
    }

    @GetMapping("/test/secured")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get test response from secured url")
    public String getTestResponseFromProtectedUrl() {
        return "This is test response from secured url";
    }

    @GetMapping("/admin//test/secured")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get test response from admin-only permitted url")
    public String getTestResponseFromAdminOnlyUrl() {
        return "This is test response from admin-only permitted url";
    }

}
