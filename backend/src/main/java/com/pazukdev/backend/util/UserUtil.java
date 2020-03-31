package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.LikeListDto;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pazukdev.backend.util.SpecificStringUtil.*;

public class UserUtil {

    public static LikeListDto createLikeListDto(final UserEntity user) {
        final LikeListDto dto = new LikeListDto();
        dto.getLikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getLikedItems()));
        dto.getDislikedItemsIds().addAll(ItemUtil.collectIds(user.getLikeList().getDislikedItems()));
        return dto;
    }

    public static UserDto getCreatorData(final Item item, final UserService service) {
        final UserEntity user = service.findOne(item.getCreatorId());
        if (user != null) {
            final UserDto creatorData = new UserDto();
            creatorData.setId(user.getId());
            creatorData.setName(user.getName());
            creatorData.setStatus(user.getStatus());
            return creatorData;
        }
        return null;
    }

    public static InputStream toCSVInputStream(final UserService service) throws IOException {
        final String lineSeparator = System.getProperty("line.separator");
        final String comma = ", ";
        final List<String> csvList = new ArrayList<>();
        final String header = "id" + comma
                + "name" + comma
                + "role" + comma
                + "rating" + comma
                + "status" + comma
                + "email" + comma
                + "password" + comma
                + "country" + comma
                + "wishlist items" + comma
                + "liked items" + comma
                + "disliked items" + comma
                + "img";
        csvList.add(header + lineSeparator);

        for (final UserEntity user : service.findAll()) {
            String items = "";
            for (final ChildItem childItem : user.getWishList().getItems()) {
                final Long id = childItem.getItem().getId();
                final String comment = isEmpty(childItem.getLocation()) ? "" : childItem.getLocation() + " - ";
                items += id + " " + "(" + comment + childItem.getQuantity() + ")" + "; ";
            }
            items = removeLastChar(items.trim());

            String likedItems = "";
            for (final Item item : user.getLikeList().getLikedItems()) {
                likedItems += item.getId() + "; ";
            }
            likedItems = removeLastChar(likedItems.trim());

            String dislikedItems = "";
            for (final Item item : user.getLikeList().getDislikedItems()) {
                dislikedItems += item.getId() + "; ";
            }
            dislikedItems = removeLastChar(dislikedItems.trim());

            final String line = user.getId() + comma
                    + user.getName() + comma
                    + user.getRole() + comma
                    + user.getRating() + comma
                    + user.getStatus() + comma
                    + replaceEmptyWithEmpty(user.getEmail()) + comma
                    + user.getPassword() + comma
                    + replaceEmptyWithEmpty(user.getCountry()) + comma
                    + replaceEmptyWithEmpty(items) + comma
                    + replaceEmptyWithEmpty(likedItems) + comma
                    + replaceEmptyWithEmpty(dislikedItems) + comma
                    + replaceEmptyWithEmpty(user.getImg());
            csvList.add(line + lineSeparator);
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (final String line : csvList) {
                baos.write(line.getBytes());
            }
            try (final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                return bais;
            }
        }

    }

}
