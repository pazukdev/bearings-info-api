package com.pazukdev.backend.util;

import com.pazukdev.backend.converter.NestedItemConverter;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.UserEntity;

import java.util.HashSet;
import java.util.Set;

public class NestedItemUtil {

    public static String createName(final String parentItemName, final String nestedItemName) {
        return parentItemName + " - " + nestedItemName;
    }

    public static Set<NestedItemDto> getLikedUserDtos(final Set<UserEntity> users) {
        final Set<NestedItemDto> userDtos = new HashSet<>();
        for (final UserEntity user : users) {
            final NestedItemDto userDto = NestedItemConverter.convert(user);
            userDto.setComment(user.getCountry());

            userDtos.add(userDto);
        }
        return userDtos;
    }

}
