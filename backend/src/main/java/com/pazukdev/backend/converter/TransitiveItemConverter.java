package com.pazukdev.backend.converter;

import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.TransitiveItemDto;
import com.pazukdev.backend.entity.TransitiveItem;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
@Data
public class TransitiveItemConverter implements EntityDtoConverter<TransitiveItem, TransitiveItemDto> {

    private final ModelMapper modelMapper;

    @Override
    public TransitiveItemDto convertToDto(final TransitiveItem item) {
        return modelMapper.map(item, TransitiveItemDto.class);
    }

    @Override
    public TransitiveItem convertToEntity(final TransitiveItemDto transitiveItemDto) {
        return modelMapper.map(transitiveItemDto, TransitiveItem.class);
    }

}
