package com.pazukdev.backend.service;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.repository.UserRepository;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.ImgUtil;
import com.pazukdev.backend.validator.UserDataValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
public class UserService extends AbstractService<UserEntity, UserDto> {

    private final PasswordEncoder passwordEncoder;
    private final UserDataValidator userDataValidator;

    public UserService(final UserRepository repository,
                       final UserConverter converter,
                       final PasswordEncoder passwordEncoder,
                       final UserDataValidator userDataValidator) {
        super(repository, converter);
        this.passwordEncoder = passwordEncoder;
        this.userDataValidator = userDataValidator;
    }

    @Transactional
    @Override
    public UserEntity findByName(final String name) {
        return ((UserRepository) repository).findByName(name);
    }

    @Transactional
    public UserEntity findByEmail(final String email) {
        return ((UserRepository) repository).findByEmail(email);
    }

    @Transactional
    @Override
    public List<UserEntity> findAll() {
        final List<UserEntity> users = super.findAll();
        users.sort(Comparator.comparing(UserEntity::getRole));
        return users;
    }

    @Transactional
    public List<String> createUser(final UserDto dto) {
        final List<String> validationMessages = userDataValidator.validateSignUpData(dto, this);
        if (validationMessages.isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            final UserEntity user = new UserEntity();
            user.setPassword(dto.getPassword());
            user.setEmail(dto.getEmail());
            user.setName(dto.getName());
            repository.save(user);
        }
        return validationMessages;
    }

    @Transactional
    public List<String> updateUser(final UserView view) {
        final String newName = view.getName();
        final String newEmail = view.getEmail();

        final UserEntity user = getOne(view.getId());
        boolean checkNameExists = newName != null && !StringUtils.equalsIgnoreCase(user.getName(), newName);
        boolean checkEmailExists = newEmail != null && !StringUtils.equalsIgnoreCase(user.getEmail(), newEmail);

        final List<String> validationMessages = new ArrayList<>();
        if (checkNameExists) {
            validationMessages.addAll(userDataValidator.validateName(newName, this));
        }
        if (checkEmailExists) {
            validationMessages.addAll(userDataValidator.validateEmail(newEmail, this));
        }

        if (validationMessages.isEmpty()) {
            user.setName(newName);
            user.setEmail(newEmail);
            user.setRole(Role.valueOf(view.getRole().toUpperCase()));
            user.setCountry(view.getCountry());
            ImgUtil.updateImg(view, user);
            repository.save(user);
        }

        return validationMessages;
    }

    @Transactional
    public Set<String> getRoles() {
        return new HashSet<>(Arrays.asList(Role.USER.name(), Role.ADMIN.name()));
    }

    @Transactional
    public boolean addItemToWishList(final Item item, final String userName) {
        final UserEntity currentUser = findByName(userName);

        final Set<Long> ids = ChildItemUtil.collectIds(currentUser.getWishList().getItems());

        if (!ids.contains(item.getId())) {
            final ChildItem childItem = new ChildItem();
            childItem.setName(ChildItemUtil.createNameForWishListItem(item.getName()));
            childItem.setItem(item);
            currentUser.getWishList().getItems().add(childItem);
            update(currentUser);
            return true;
        } else {
            return false;
        }

    }

    public UserEntity getAdmin() {
        return getOne(2L);
    }

}
