package com.pazukdev.backend.converter;

import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.TransitiveItemDto;
import com.pazukdev.backend.entity.Item;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
@Data
public class ItemConverter implements EntityDtoConverter<Item, TransitiveItemDto> {

    private final ModelMapper modelMapper;

    @Override
    public TransitiveItemDto convertToDto(final Item item) {
        return modelMapper.map(item, TransitiveItemDto.class);
    }

    @Override
    public Item convertToEntity(final TransitiveItemDto transitiveItemDto) {
        return modelMapper.map(transitiveItemDto, Item.class);
    }

}