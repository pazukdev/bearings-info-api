package com.pazukdev.backend.validator;

import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.repository.UserRepository;
import com.pazukdev.backend.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class UserDataValidator {

    public static final List<String> exceptions = new ArrayList<>(Arrays.asList("default"));
    public static final List<String> forbiddenSubstrings = new ArrayList<>(Arrays.asList(";", ":", "*"));

    public List<String> validateSignUpData(final UserDto user, final UserService service) {
        final String name = user.getName();

        final List<String> messages = new ArrayList<>();
        messages.addAll(validatePassword(user.getPassword(), user.getRepeatedPassword(), name, service));
        messages.addAll(validateName(name, service));
        messages.addAll(validateEmail(user.getEmail(), service));

        return messages;
    }

    public List<String> validatePassword(final String password,
                                         final String repeatedPassword,
                                         final String userName,
                                         final UserService service) {
        final List<String> messages = new ArrayList<>();
        if (isEmpty(password)) {
            messages.add("Password is empty");
        }
        if (isEmpty(repeatedPassword)) {
            messages.add("Repeated password is empty");
        } else if (!repeatedPassword.equals(password)) {
            messages.add("Passwords are different");
        }
        messages.addAll(checkForbiddenSubstrings(userName));
        return messages;
    }

    public List<String> validateChangedPassword(final UserView view,
                                                final PasswordEncoder passwordEncoder,
                                                final boolean currentUserIsAdmin,
                                                final UserService service) {
        final String oldPassword = view.getOldPassword();
        final String password = view.getNewPassword();
        final String repeatedPassword = view.getRepeatedNewPassword();

        final List<String> messages = new ArrayList<>();
        if (!currentUserIsAdmin) {
            if (isEmpty(oldPassword)) {
                messages.add("Old password is empty");
            } else {
                final UserEntity user = service.findOne(view.getId());
                if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                    messages.add("Old password is incorrect");
                }
            }
        }
        messages.addAll(validatePassword(password, repeatedPassword, view.getName(), service));
        return messages;
    }

    public List<String> validateName(final String userName, final UserService service) {
        final List<String> messages = new ArrayList<>();
        if (isEmpty(userName)) {
            messages.add("Nickname is empty");
        }
        messages.addAll(checkForbiddenSubstrings(userName));
        if (messages.isEmpty() && service.findFirstByName(userName) != null) {
            messages.add(createUserExistsMessage("name"));
        }
        return messages;
    }

    public List<String> validateEmail(final String email, final UserService userService) {
        final List<String> messages = new ArrayList<>();
        if (!EmailValidator.getInstance().isValid(email)) {
            messages.add("Invalid email");
        }
        messages.addAll(checkForbiddenSubstrings(email));
        if (messages.isEmpty() && ((UserRepository) userService.getRepository()).findFirstByEmail(email) != null) {
            messages.add(createUserExistsMessage("email"));
        }
        return messages;
    }

    public List<String> checkForbiddenSubstrings(final String value) {
        final List<String> messages = new ArrayList<>();
        for (final String s : forbiddenSubstrings) {
            if (value.contains(s)) {
                messages.add("Value shouldn't contain: " + s);
            }
        }
        if (exceptions.contains(value.toLowerCase())) {
            messages.add("Value shouldn't be: " + value);
        }

        return messages;
    }

    private String createUserExistsMessage(final String userParameter) {
        return "User with this " + userParameter + " already exists";
    }

}
