package com.pazukdev.backend.service;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.converter.UserConverter;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.repository.ChildItemRepository;
import com.pazukdev.backend.repository.UserRepository;
import com.pazukdev.backend.repository.WishListRepository;
import com.pazukdev.backend.util.FileUtil;
import com.pazukdev.backend.util.ImgUtil;
import com.pazukdev.backend.validator.UserDataValidator;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pazukdev.backend.util.ChildItemUtil.collectIds;
import static com.pazukdev.backend.util.ChildItemUtil.createNameForWishListItem;
import static com.pazukdev.backend.util.SpecificStringUtil.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
@Getter
public class UserService extends AbstractService<UserEntity, UserDto> {

    private static final String ADMIN_NAME = "admin";
    private static final Long ADMIN_ID = 2L;

    private final PasswordEncoder passwordEncoder;
    private final UserDataValidator userDataValidator;
    private final WishListRepository wishListRepository;
    private final ChildItemRepository childItemRepository;

    public UserService(final UserRepository repository,
                       final UserConverter converter,
                       final PasswordEncoder passwordEncoder,
                       final UserDataValidator userDataValidator,
                       final WishListRepository wishListRepository,
                       final ChildItemRepository childItemRepository) {
        super(repository, converter);
        this.passwordEncoder = passwordEncoder;
        this.userDataValidator = userDataValidator;
        this.wishListRepository = wishListRepository;
        this.childItemRepository = childItemRepository;
    }

    public UserRepository getRepository() {
        return (UserRepository) repository;
    }

    public void save(final List<UserEntity> users) {
        for (final UserEntity user : users) {
            repository.save(user);
        }
    }

    @Transactional
    public UserEntity findActiveByName(final String name) {
        return ((UserRepository) repository).findFirstByNameAndStatus(name, Status.ACTIVE);
    }

    @Transactional
    @Override
    public UserEntity findFirstByName(final String name) {
        return ((UserRepository) repository).findFirstByName(name);
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
        boolean admin = isAdmin(findActiveByName(view.getCurrentUserName()));

        final String newName = view.getName();
        final String newEmail = view.getEmail();

        final UserEntity user = findOne(view.getId());
        final boolean changeName = newName!= null && !user.getName().equals(newName);
        final boolean changeEmail = newEmail != null && !StringUtils.equalsIgnoreCase(user.getEmail(), newEmail);
        final boolean changePassword = admin ? view.getNewPassword() != null : view.getOldPassword() != null;

        final List<String> validationMessages = new ArrayList<>();
        if (changeName) {
            validationMessages.addAll(userDataValidator.validateName(newName, this));
        }
        if (changeEmail) {
            validationMessages.addAll(userDataValidator.validateEmail(newEmail, this));
        }
        if (changePassword) {
            validationMessages.addAll(userDataValidator.validateChangedPassword(view, passwordEncoder, admin, this));
        }

        if (validationMessages.isEmpty()) {
            if (changeName) {
                user.setName(newName);
            }
            if (changeEmail) {
                user.setEmail(newEmail);
            }
            if (changePassword) {
                user.setPassword(passwordEncoder.encode(view.getNewPassword()));
            }
            user.setRole(Role.valueOf(view.getRole().toUpperCase()));
            user.setCountry(view.getCountry());
            ImgUtil.updateImg(view, user);
            user.setStatus(view.getStatus());

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
        final UserEntity currentUser = findFirstByName(userName);

        final Set<Long> ids = collectIds(currentUser.getWishList().getItems());

        if (!ids.contains(item.getId())) {
            final ChildItem childItem = new ChildItem();
            childItem.setName(createNameForWishListItem(item.getName()));
            childItem.setItem(item);
            currentUser.getWishList().getItems().add(childItem);
            update(currentUser);
            return true;
        }

        return  false;
    }

    public UserEntity getAdmin() {
        return findOne(ADMIN_ID);
    }

    public UserEntity findAdmin(final List<UserEntity> users) {
        return findFirstByName(ADMIN_NAME, users);
    }

    public boolean isAdmin(final UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    public List<UserEntity> getUsersFromRecoveryFile(final boolean recoverUserData) {
        final List<UserEntity> users = new ArrayList<>();
        final String usersFileId = "1lQvD9rQYheddn-D1k8JmnUPb7fRJr6TkJVdcwPzdTmo";
        final List<List<String>> rows = FileUtil.readGoogleDocSpreadsheet(usersFileId);
        final List<String> header = rows.get(0);
        rows.remove(0);
        for (final List<String> userData : rows) {
            final Long id = Long.valueOf(userData.get(header.indexOf("id")));
            UserEntity user = repository.findById(id).orElse(null);
            if (user != null && !recoverUserData) {
                continue;
            } else if (user == null) {
                user = new UserEntity();
                user.setWishList(new WishList());
                user.setLikeList(new LikeList());
            }
            user.setId(Long.valueOf(getValue("id", header, userData)));
            user.setRole(Role.valueOf(getValue("role", header, userData)));
            user.setName(getValue("name", header, userData));
            user.setRating(Integer.valueOf(getValue("rating", header, userData)));
            user.setStatus(getValue("status", header, userData));
            user.setEmail(getValue("email", header, userData));
            user.setPassword(getValue("password", header, userData));
            user.setCountry(getValue("country", header, userData));
            user.setImg(getValue("img", header, userData));

            users.add(user);
        }
        return users;
    }

    private String getValue(final String key, final List<String> header, final List<String> userData) {
        return replaceEmptyWithDash(userData.get(header.indexOf(key)));
    }

    private List<ChildItem> recoverWishlistItems(final String source, final ItemService itemService) {
        final List<ChildItem> items = new ArrayList<>();
        if (isEmpty(source)) {
            return items;
        }
        for (String s : source.split(";")) {
            s = s.trim();
            final Long itemId;
            String location = "";
            String quantity;
            if (containsParentheses(s)) {
                itemId = Long.valueOf(getStringBeforeParentheses(s));
                String additionalData = getStringBetweenParentheses(s);
                location = additionalData.contains(" - ") ? additionalData.split(" - ")[0] : "-";
                quantity = additionalData.contains(" - ") ? additionalData.split(" - ")[1] : additionalData;
            } else {
                itemId = Long.valueOf(s);
                location = "-";
                quantity = "1";
            }

            final Item item = itemService.getRepository().findById(itemId).orElse(null);
            if (item == null) {
                continue;
            }

            final ChildItem wishlistItem = new ChildItem();
            wishlistItem.setName("Wishlist - " + item.getName());
            wishlistItem.setItem(item);
            wishlistItem.setLocation(location);
            wishlistItem.setQuantity(quantity);

            items.add(wishlistItem);

        }
        return items;
    }

    public void recoverUserActions(final List<UserEntity> users, final ItemService itemService) {
        final String usersFileId = "1lQvD9rQYheddn-D1k8JmnUPb7fRJr6TkJVdcwPzdTmo";
        final List<List<String>> rows = FileUtil.readGoogleDocSpreadsheet(usersFileId);
        final List<String> header = rows.get(0);
        rows.remove(0);

        final Map<Long, List<ChildItem>> wishlistItems = new HashMap<>();
        final Map<Long, List<Item>> likedItems = new HashMap<>();
        final Map<Long, List<Item>> dislikedItems = new HashMap<>();

        for (final List<String> userData : rows) {
            final Long id = Long.valueOf(userData.get(header.indexOf("id")));
            wishlistItems.put(id, recoverWishlistItems(getValue("wishlist items", header, userData), itemService));
            likedItems.put(id, itemService.getItems(getValue("liked items", header, userData)));
            dislikedItems.put(id, itemService.getItems(getValue("disliked items", header, userData)));
        }

        for (final UserEntity user : users) {
            user.getWishList().getItems().clear();
            user.getLikeList().getLikedItems().clear();
            user.getLikeList().getDislikedItems().clear();

            final Long userId = user.getId();
            user.getWishList().getItems().addAll(wishlistItems.get(userId));
            user.getLikeList().getLikedItems().addAll(likedItems.get(userId));
            user.getLikeList().getDislikedItems().addAll(dislikedItems.get(userId));
        }
    }

}
