package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.entity.UserEntity;

/**
 * @author Siarhei Sviarkaltsau
 */
public class UserDtoFactory {

    public static UserDto createItemViewUserData(final UserEntity user) {
        return create(user);
    }

    public static UserDto create(final UserEntity user) {
        final UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setRole(user.getRole().name());
        dto.setEmail(user.getEmail());
        dto.setRating(user.getRating());
        dto.setStatus(user.getStatus());
        dto.setWishListId(user.getWishList().getId());
        return dto;
    }

}
