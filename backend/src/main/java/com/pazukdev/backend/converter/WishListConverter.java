package com.pazukdev.backend.converter;

import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.WishListDto;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.WishList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class WishListConverter implements EntityDtoConverter<WishList, WishListDto> {

    private final ModelMapper modelMapper;

    public WishListConverter(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public WishListDto convertToDto(final WishList wishList) {
        final WishListDto wishListDto = modelMapper.map(wishList, WishListDto.class);
//        for (final TransitiveItem item : wishList.getItems()) {
//            wishListDto.getItemIds().add(item.getId());
//        }
        return wishListDto;
    }

    @Override
    public WishList convertToEntity(final WishListDto wishListDto) {
        final WishList wishList = modelMapper.map(wishListDto, WishList.class);
        for (final Long itemId : wishListDto.getItemIds()) {
            final TransitiveItem item = new TransitiveItem();
            item.setId(itemId);
//            wishList.getItems().add(item);
        }
        return wishList;
    }

}
