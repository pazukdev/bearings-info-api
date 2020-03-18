package com.pazukdev.backend.converter;

import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.Replacer;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
@Data
public class ReplacerConverter implements EntityDtoConverter<Replacer, NestedItemDto> {

    private final ModelMapper modelMapper;

    @Override
    public NestedItemDto convertToDto(final Replacer replacer) {
        final NestedItemDto dto = modelMapper.map(replacer, NestedItemDto.class);
        dto.setId(replacer.getId());
        return dto;
    }

    @Override
    public Replacer convertToEntity(final NestedItemDto dto) {
        return modelMapper.map(dto, Replacer.class);
    }

}
