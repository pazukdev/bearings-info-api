package com.pazukdev.backend.dataloader;

import com.pazukdev.backend.dto.ReplacerData;
import com.pazukdev.backend.entity.*;
import com.pazukdev.backend.entity.factory.TransitiveItemFactory;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pazukdev.backend.util.UserActionUtil.ActionType;
import static com.pazukdev.backend.util.UserActionUtil.createAction;

/**
 * @author Siarhei Sviarkaltsau
 *
 * the class populates all empty tables in db with default data on app startup
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final TransitiveItemFactory transitiveItemFactory;
    @Getter
    private final ItemService itemService;

    @Override
    public void run(ApplicationArguments args) {
        final boolean initialDBPopulation = true;
        populate(initialDBPopulation);
    }

    @Transactional
    public void populate(final boolean initialDBPopulation) {
        if (initialDBPopulation && !itemService.getRepository().findAll().isEmpty()) {
            return;
        }
        final long start = System.nanoTime();

        final List<String> infoCategories = FileUtil.readGoogleDocDocument(FileUtil.FileId.INFO_CATEGORY);

        final List<UserEntity> users = itemService.getUserService().findAll();
        if (initialDBPopulation) {
            users.addAll(itemService.getUserService().getUsersFromRecoveryFile(true));
        }
        final List<TransitiveItem> transitiveItems = createStubReplacers(transitiveItemFactory.createEntitiesFromCSVFile());
        final UserEntity admin = itemService.getUserService().findAdmin(users);

        final Map<Item, List<NestedItem>> itemsReplacers = new HashMap<>();
        for (final TransitiveItem transitiveItem : transitiveItems) {
            itemService.convertTransitiveItemToItem(
                    transitiveItem,
                    transitiveItems,
                    infoCategories,
                    admin,
                    initialDBPopulation,
                    itemsReplacers);
        }
        itemService.getUserService().recoverUserActions(users, itemService);
        itemService.getUserService().save(users);

        final List<UserAction> actions = new ArrayList<>();
        Pageable p = RepositoryUtil.getPageRequest(RepositoryUtil.LAST_TEN);

        final String category = CategoryUtil.Category.VEHICLE;
        for (final Item item : itemService.getItemRepository().findFirst10ByCategory(category, p)) {
            final UserEntity creator = itemService.getUserService().findFirst(item.getCreatorId());
            actions.add(createAction(ActionType.CREATE, "", null, item, creator, false));
        }

        final List<NestedItem> replacers = itemService.getNestedItemRepo().findAll();
        Collections.reverse(itemService.getNestedItemRepo().findAll());

        int oilFilter = 0;
        int seal = 0;
        int bearing = 0;
        for (final NestedItem replacer : replacers) {
            final UserEntity creator = itemService.getUserService().findFirst(replacer.getCreatorId());
            if (replacer.getType().equals(NestedItem.Type.PART.name().toLowerCase())) {
                continue;
            }
            final String itemCategory = replacer.getItem().getCategory();
            boolean add = false;
            if (itemCategory.equals("Oil filter") && oilFilter < 3) {
                add = true;
                oilFilter++;

            } else if (itemCategory.equals("Bearing") && bearing < 3) {
                add = true;
                bearing++;

            } else if (itemCategory.equals("Seal") && seal < 4) {
                add = true;
                seal++;
            }
            if (add) {
                Item parent = null;
                for (final Map.Entry<Item, List<NestedItem>> entry : itemsReplacers.entrySet()) {
                    for (final NestedItem r : entry.getValue()) {
                        if (r.getId().equals(replacer.getId())) {
                            parent = entry.getKey();
                            break;
                        }
                    }
                }
                actions.add(createAction(
                        ActionType.ADD,
                        "",
                        parent,
                        replacer,
                        creator,
                        false));
            }
        }

        LoggerUtil.warn(actions, itemService.getUserActionRepo(), null, null, null);

        final long stop = System.nanoTime();
        final double time = (stop - start) * 0.000000001;
        LoggerUtil.info("DB created in " + (int) time + " seconds");
    }

    public Item findParentForReplacer(final NestedItem replacer, final List<Item> checkList) {
        for (final Item parent : checkList) {
            for (final NestedItem r : parent.getReplacers()) {
                if (r.getId().equals(replacer.getId())) {
                    return parent;
                }
            }
        }
        return null;
    }

    public void updateItem(final String category, final String name) {
        final String itemInfo = "item category=" + category + " name=" + name;
        final List<String> infoCategories = FileUtil.readGoogleDocDocument(FileUtil.FileId.INFO_CATEGORY);
        final List<UserEntity> users = itemService.getUserService().findAll();
        final List<TransitiveItem> transitiveItems = createStubReplacers(transitiveItemFactory.createEntitiesFromCSVFile());
        final UserEntity admin = itemService.getUserService().findAdmin(users);
        for (final TransitiveItem transitiveItem : transitiveItems) {
            if (transitiveItem.getCategory().equalsIgnoreCase(category)
                    && transitiveItem.getName().equalsIgnoreCase(name)) {
                itemService.convertTransitiveItemToItem(
                        transitiveItem,
                        transitiveItems,
                        infoCategories,
                        admin,
                        false,
                        new HashMap<>());
                LoggerUtil.info("Updated successfully: " + itemInfo);
                return;
            }
        }
        LoggerUtil.warn("Not updated: " + itemInfo + " not found");
    }

    private List<TransitiveItem> createStubReplacers(final List<TransitiveItem> items) {
        final List<TransitiveItem> stubReplacers = new ArrayList<>();
        for (final TransitiveItem item : items) {
            final List<TransitiveItem> newItems = createStubReplacers(item, items);
            stubReplacers.addAll(newItems);
        }
        items.addAll(stubReplacers);
        return items;
    }

    private List<TransitiveItem> createStubReplacers(final TransitiveItem item, final List<TransitiveItem> items) {
        final List<TransitiveItem> newItems = new ArrayList<>();
        final String category = item.getCategory();
        final Map<String, String> descriptionMap = ItemUtil.toMap(item.getDescription());
        final List<ReplacerData> replacersData = ItemUtil.parseReplacersSourceString(item.getReplacer());

        for (final ReplacerData replacerData : replacersData) {
            final String name = replacerData.getName();
            TransitiveItem replacer = TransitiveItemUtil.findFirstByCategoryAndName(category, name, items);
            if (replacer == null && category.equals("Rubber part")) {
                replacer = TransitiveItemUtil.findFirstByCategoryAndName("Bearing", name, items);
            }
            if (replacer == null) {
                final TransitiveItem stubReplacer = new TransitiveItem();
                stubReplacer.setName(replacerData.getName());
                stubReplacer.setReplacer("-");
                stubReplacer.setCategory(category);
                stubReplacer.setImage("-");
                if (category.equalsIgnoreCase("bearing")) {
                    BearingUtil.setBearingEnclosure(stubReplacer);
                    removeValues(descriptionMap, "Manufacturer", "Standard");
                } else {
                    removeValues(descriptionMap, "Manufacturer", "Standard", "Material", "Screw class");
                }

                stubReplacer.setDescription(ItemUtil.toDescription(descriptionMap));
                newItems.add(stubReplacer);
            }
        }
        return newItems;
    }

    private void removeValues(final Map<String, String> descriptionMap, final String... keys) {
        for (final String key : keys) {
            if (descriptionMap.get(key) != null) {
                descriptionMap.put(key, "-");
            }
        }
    }

}
















