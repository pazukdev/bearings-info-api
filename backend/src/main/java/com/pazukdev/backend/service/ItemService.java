package com.pazukdev.backend.service;

import com.pazukdev.backend.converter.ItemConverter;
import com.pazukdev.backend.converter.ReplacerConverter;
import com.pazukdev.backend.dto.ItemView;
import com.pazukdev.backend.dto.TransitiveItemDescriptionMap;
import com.pazukdev.backend.dto.TransitiveItemDto;
import com.pazukdev.backend.dto.factory.ItemViewFactory;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.repository.ChildItemRepository;
import com.pazukdev.backend.repository.ItemRepository;
import com.pazukdev.backend.repository.ReplacerRepository;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.util.CategoryUtil;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.DateUtil;
import com.pazukdev.backend.util.ItemUtil;
import com.pazukdev.backend.util.ReplacerUtil;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pazukdev.backend.util.ItemUtil.createDescriptionMap;
import static com.pazukdev.backend.util.SpecificStringUtil.replaceBlankWithDash;

/**
 * @author Siarhei Sviarkaltsau
 */
@Getter
@Service
public class ItemService extends AbstractService<Item, TransitiveItemDto> {

    private final TransitiveItemService transitiveItemService;
    private final UserService userService;
    private final ChildItemRepository childItemRepository;
    private final UserActionRepository userActionRepository;
    private final ReplacerRepository replacerRepository;
    private final ReplacerConverter replacerConverter;

    public ItemService(final ItemRepository itemRepository,
                       final ItemConverter converter,
                       final TransitiveItemService transitiveItemService,
                       final ChildItemRepository childItemRepository,
                       final UserService userService,
                       final UserActionRepository userActionRepository,
                       final ReplacerRepository replacerRepository,
                       final ReplacerConverter replacerConverter) {
        super(itemRepository, converter);
        this.transitiveItemService = transitiveItemService;
        this.childItemRepository = childItemRepository;
        this.userService = userService;
        this.userActionRepository = userActionRepository;
        this.replacerRepository = replacerRepository;
        this.replacerConverter = replacerConverter;
    }

    @Transactional
    @Override
    public Item findByName(String name) {
        return ((ItemRepository) repository).findByName(name);
    }

    @Transactional
    public Item find(final String category, final String name) {
        for (final Item item : find(category)) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Transactional
    public List<Item> find(final String category) {
        return find(category, findAll());
    }

    @Transactional
    public List<Item> find(final String category, final List<Item> items) {
        final List<Item> categorizedItems = new ArrayList<>();
        for (final Item item : items) {
            if (item.getCategory().toLowerCase().equals(category.toLowerCase())) {
                categorizedItems.add(item);
            }
        }
        return categorizedItems;
    }

    @Transactional
    public Item saveAsItem(final TransitiveItem transitiveItem) {
        final Item item = getOrCreate(transitiveItem);
        item.setName(replaceBlankWithDash(item.getName()));
        return repository.save(item);
    }

    @Transactional
    public ItemView createItemView(final Long itemId, final String userName) {
        final ItemViewFactory itemViewFactory = new ItemViewFactory(this);
        return itemViewFactory.createItemView(itemId, userName);
    }

    @Transactional
    public ItemView createNewItemView(final String category,
                                      final String name,
                                      final String userName) {
        final ItemViewFactory itemViewFactory = new ItemViewFactory(this);
        return itemViewFactory.createNewItemView(category, name, userName);
    }

    @Transactional
    public ItemView updateItemView(final Long itemId, final String userName, final ItemView itemView) {
        final ItemViewFactory itemViewFactory = new ItemViewFactory(this);
        return itemViewFactory.updateItemView(itemId, userName, itemView);
    }

    public Item getOrCreate(final TransitiveItem transitiveItem) {
        final Item item = find(transitiveItem.getCategory(), transitiveItem.getName());
        return item != null ? item : create(transitiveItem);
    }

    public Item create(final TransitiveItem transitiveItem) {
        final TransitiveItemDescriptionMap descriptionMap
                = createDescriptionMap(transitiveItem, transitiveItemService);
        final Map<String, String> items = descriptionMap.getItems();
        final List<ChildItem> childItems
                = ChildItemUtil.createParts(transitiveItem, items, this, transitiveItemService);
        final List<Replacer> replacers
                = ReplacerUtil.createReplacers(transitiveItem, this, transitiveItemService);

        final Item item = new Item();
        item.setName(transitiveItem.getName());
        item.setCategory(transitiveItem.getCategory().replace(" (i)", ""));
        item.setStatus("active");
        item.setDescription(createItemDescription(transitiveItem));
        item.getChildItems().addAll(childItems);
        item.getReplacers().addAll(replacers);
        item.setCreatorId(userService.getAdmin().getId());
        item.setUserActionDate(DateUtil.now());
        if (transitiveItem.getImage() != null) {
            item.setImage(transitiveItem.getImage());
        }
        return item;
    }

    public Set<String> findCategories(final List<Item> items) {
        final Set<String> categories = new HashSet<>();
        for (final Item item : items) {
            categories.add(item.getCategory());
        }
        return categories;
    }

    public Set<String> findAllCategories() {
        return findCategories(findAll());
    }

    public Set<String> findAllPartCategories() {
        return CategoryUtil.filterPartCategories(findAllCategories());
    }

    private String createItemDescription(final TransitiveItem transitiveItem) {
        final TransitiveItemDescriptionMap descriptionMap = createDescriptionMap(transitiveItem, transitiveItemService);
        descriptionMap.getItems().clear();
        return ItemUtil.toDescription(descriptionMap);
    }

}
