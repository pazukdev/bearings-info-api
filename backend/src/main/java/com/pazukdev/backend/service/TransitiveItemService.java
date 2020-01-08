package com.pazukdev.backend.service;

import com.pazukdev.backend.converter.TransitiveItemConverter;
import com.pazukdev.backend.dto.TransitiveItemDto;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.repository.TransitiveItemRepository;
import com.pazukdev.backend.util.CategoryUtil;
import com.pazukdev.backend.util.ItemUtil;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
public class TransitiveItemService extends AbstractService<TransitiveItem, TransitiveItemDto> {

    @Getter
    private final TransitiveItemRepository transitiveItemRepository;

    public TransitiveItemService(final TransitiveItemRepository transitiveItemRepository,
                                 final TransitiveItemConverter converter) {
        super(transitiveItemRepository, converter);
        this.transitiveItemRepository = transitiveItemRepository;
    }

    public TransitiveItem getUssrSealBySize(final String searchingSize) {
        final List<TransitiveItem> ussrSeals = filter(find("Seal"), "Manufacturer", "USSR");
        for (TransitiveItem seal : ussrSeals) {
            final String actualSize = ItemUtil.getValueFromDescription(seal.getDescription(), "Size, mm");
            if (actualSize.equals(searchingSize)) {
                return seal;
            }
        }
        return null;
    }

    private boolean isSealSize(final String value) {
        final List<String> dimensions = new ArrayList<>(Arrays.asList(value.split("x")));
        if (value.length() < 2) {
            return false;
        }
        try {
            Integer.valueOf(dimensions.get(0));
            Integer.valueOf(dimensions.get(1));
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    private List<TransitiveItem> filter(final List<TransitiveItem> items,
                                        final String parameter,
                                        final String searchingValue) {
        final List<TransitiveItem> filteredItems = new ArrayList<>();
        for (TransitiveItem item : items) {
            final String value = ItemUtil.getValueFromDescription(item.getDescription(), parameter);
            if (value != null && value.equals(searchingValue)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    @Transactional
    @Override
    public TransitiveItem findByName(final String name) {
        return transitiveItemRepository.findByName(name);
    }

    @Transactional
    public TransitiveItem find(final String category, final String name) {
        if (category == null || name == null) {
            return null;
        }

        if (category.equalsIgnoreCase("seal") && isSealSize(name)) {
            return getUssrSealBySize(name);
        }

        for (final TransitiveItem item : find(category)) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Transactional
    public List<TransitiveItem> find(final String category) {
        final List<TransitiveItem> categorizedItems = new ArrayList<>();
        for (final TransitiveItem item : findAll()) {
            if (item.getCategory().toLowerCase().equals(category.toLowerCase())) {
                categorizedItems.add(item);
            }
        }
        return categorizedItems;
    }

    public Set<String> findAllCategories() {
        return findCategories(transitiveItemRepository.findAll());
    }

    public Set<String> findInfoCategories() {
        return CategoryUtil.filterInfoCategories(findAllCategories());
    }

    public Set<String> findCategories(final List<TransitiveItem> items) {
        final Set<String> categories = new HashSet<>();
        for (final TransitiveItem item : items) {
            categories.add(item.getCategory());
        }
        return categories;
    }

}




















