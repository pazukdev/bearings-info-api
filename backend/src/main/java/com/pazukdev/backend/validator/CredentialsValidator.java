package com.pazukdev.backend.validator;

import com.pazukdev.backend.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class CredentialsValidator {

    public List<String> validate(final UserDto dto, final boolean userExists) {
        final List<String> validationMessages = new ArrayList<>();
        final String email = dto.getName();

        if (!EmailValidator.getInstance().isValid(email)) {
            validationMessages.add("Invalid email");
        }
        if (StringUtils.isBlank(dto.getName())) {
            validationMessages.add("Login is empty");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            validationMessages.add("Password is empty");
        }
        if (!dto.getRepeatedPassword().equals(dto.getPassword())) {
            validationMessages.add("Passwords are different");
        }
        if (userExists) {
            validationMessages.add("User with this Login already exists");
        }

        return validationMessages;
    }

}
