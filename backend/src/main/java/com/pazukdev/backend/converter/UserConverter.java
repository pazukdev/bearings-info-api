package com.pazukdev.backend.converter;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.repository.WishListRepository;
import com.pazukdev.backend.util.ImgUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class UserConverter implements EntityDtoConverter<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    public UserConverter(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto convertToDto(final UserEntity user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserEntity convertToEntity(final UserDto dto) {
        return modelMapper.map(dto, UserEntity.class);
    }

    public static UserDto convert(final UserEntity user) {
        final UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setRole(user.getRole().name());
        userDto.setEmail(user.getEmail());
        userDto.setRating(user.getRating());
        userDto.setStatus(user.getStatus());
        userDto.setCountry(user.getCountry());
        userDto.setImg(user.getImg());
        userDto.setPassword("-");
        userDto.setWishListId(user.getWishList().getId());
        return userDto;
    }

    public static UserEntity convert(final UserDto userDto, final WishListRepository repo) {
        final UserEntity user = new UserEntity();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setRole(Role.valueOf(userDto.getRole().toUpperCase()));
        user.setEmail(userDto.getEmail());
        user.setRating(userDto.getRating());
        user.setStatus(userDto.getStatus());
        user.setCountry(userDto.getCountry());
        if (!isEmpty(userDto.getPassword())) {
            user.setPassword(userDto.getPassword());
        }
        user.setImg(userDto.getImg());
        if (userDto.getWishListId() != null && userDto.getWishListId() > 0) {
            repo.findById(userDto.getWishListId()).ifPresent(user::setWishList);
        }
        return user;
    }

    public static UserView convertToUserView(final UserEntity user) {
        final ImgViewData imgViewData = ImgUtil.getImg(user);

        final UserView userView = new UserView();
        userView.setId(user.getId());
        userView.setName(user.getName());
        userView.setRole(user.getRole().toString().toLowerCase());
        userView.setRating(user.getRating().toString());
        userView.setEmail(user.getEmail());
        userView.setImg(imgViewData.getImg());
        userView.setDefaultImg(imgViewData.getDefaultImg());
        userView.setCountry(user.getCountry());
        userView.setStatus(user.getStatus());
        return userView;
    }

}
