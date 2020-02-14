package com.pazukdev.backend.service;

import com.pazukdev.backend.converter.WishListConverter;
import com.pazukdev.backend.dto.WishListDto;
import com.pazukdev.backend.entity.WishList;
import com.pazukdev.backend.repository.WishListRepository;
import org.springframework.stereotype.Service;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
public class WishListService extends AbstractService<WishList, WishListDto> {

    public WishListService(final WishListRepository repository, final WishListConverter converter) {
        super(repository, converter);
    }

    @Override
    public WishList findFirstByName(final String name) {
        return ((WishListRepository) repository).findByName(name);
    }

}
