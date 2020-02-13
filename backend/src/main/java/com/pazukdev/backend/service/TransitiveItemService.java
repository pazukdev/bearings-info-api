package com.pazukdev.backend.service;

import com.pazukdev.backend.converter.TransitiveItemConverter;
import com.pazukdev.backend.dto.TransitiveItemDto;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.repository.TransitiveItemRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pazukdev.backend.util.CategoryUtil.isInfo;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
public class TransitiveItemService extends AbstractService<TransitiveItem, TransitiveItemDto> {

    @Getter
    private final TransitiveItemRepository transitiveItemRepository;

    public TransitiveItemService(final TransitiveItemRepository repository, final TransitiveItemConverter converter) {
        super(repository, converter);
        this.transitiveItemRepository = repository;
    }

    @Transactional
    @Override
    public TransitiveItem findFirstByName(final String name) {
        return transitiveItemRepository.findFirstByName(name);
    }

    @Transactional
    public TransitiveItem find(final String category, final String name) {
        return transitiveItemRepository.findFirstByCategoryAndName(category, name);
    }

    public boolean isPart(String parameter, final List<String> infoCategories) {
        if (isInfo(parameter, infoCategories)) {
            return false;
        }
        return transitiveItemRepository.findFirstByCategory(parameter) != null;
    }

}




















