package com.pazukdev.backend.validator;

import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class UserDataValidator {

    public static final List<String> exceptions = new ArrayList<>(Arrays.asList("default"));
    public static final List<String> forbiddenSubstrings = new ArrayList<>(Arrays.asList(";", ":", "*"));

    public List<String> validateSignUpData(final UserDto user, final UserService service) {
        final List<String> validationMessages = new ArrayList<>();

        if (StringUtils.isBlank(user.getPassword())) {
            validationMessages.add("Password is empty");
        }
        if (!user.getRepeatedPassword().equals(user.getPassword())) {
            validationMessages.add("Passwords are different");
        }
        validationMessages.addAll(validateName(user.getName(), service));
        validationMessages.addAll(validateEmail(user.getEmail(), service));

        return validationMessages;
    }

    public List<String> validateName(final String userName, final UserService service) {
        final List<String> validationMessages = new ArrayList<>();
        if (StringUtils.isBlank(userName)) {
            validationMessages.add("Nickname is empty");
        }
        validationMessages.addAll(checkForbiddenSubstrings(userName));
        if (validationMessages.isEmpty() && service.findByName(userName) != null) {
            validationMessages.add(createUserExistsMessage("name"));
        }
        return validationMessages;
    }

    public List<String> validateEmail(final String email, final UserService userService) {
        final List<String> validationMessages = new ArrayList<>();
        if (!EmailValidator.getInstance().isValid(email)) {
            validationMessages.add("Invalid email");
        }
        validationMessages.addAll(checkForbiddenSubstrings(email));
        if (validationMessages.isEmpty() && userService.findByEmail(email) != null) {
            validationMessages.add(createUserExistsMessage("email"));
        }
        return validationMessages;
    }

    public List<String> checkForbiddenSubstrings(final String value) {
        final List<String> validationMessages = new ArrayList<>();
        for (final String s : forbiddenSubstrings) {
            if (value.contains(s)) {
                validationMessages.add("Value shouldn't contain: " + s);
            }
        }
        if (exceptions.contains(value.toLowerCase())) {
            validationMessages.add("Value shouldn't be: " + value);
        }

        return validationMessages;
    }

    private String createUserExistsMessage(final String userParameter) {
        return "User with this " + userParameter + " already exists";
    }

}
