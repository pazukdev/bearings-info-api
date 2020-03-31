package com.pazukdev.backend.dataloader;

import com.pazukdev.backend.dto.ReplacerData;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.entity.factory.TransitiveItemFactory;
import com.pazukdev.backend.repository.TransitiveItemRepository;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;
import com.pazukdev.backend.util.BearingUtil;
import com.pazukdev.backend.util.FileUtil;
import com.pazukdev.backend.util.ItemUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Siarhei Sviarkaltsau
 *
 * the class populates all empty tables in db with default data on app startup
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger("DataLoader");

    private final TransitiveItemFactory transitiveItemFactory;
    private final TransitiveItemService transitiveItemService;
    private final ItemService itemService;

    @Override
    public void run(ApplicationArguments args) {
        populateEmptyTables();
    }

    @Transactional
    public void populateEmptyTables() {
        if (!repositoryIsEmpty(transitiveItemService.getTransitiveItemRepository())) {
            return;
        }
        final long start = System.nanoTime();

        final List<String> infoCategories = FileUtil.readGoogleDocDocument(FileUtil.FileName.INFO_CATEGORIES);

        final List<UserEntity> users = itemService.getUserService().getUsersFromRecoveryFile(true);
        createTransitiveItems();
        createItems(infoCategories, users);
        itemService.getUserService().recoverUserActions(users, itemService);
        itemService.getUserService().save(users);

        final long stop = System.nanoTime();
        final double time = (stop - start) * 0.000000001;
        LOG.info("DB created in " + (int) time + " seconds");
    }

    private boolean repositoryIsEmpty(final TransitiveItemRepository repository) {
        return repository.findAll().isEmpty();
    }

    private void createTransitiveItems() {
        final List<TransitiveItem> transitiveItems = transitiveItemFactory.createEntitiesFromCSVFile();
        saveTransitiveItems(transitiveItems);
        createStubReplacers(transitiveItems);
    }

    private void createItems(final List<String> infoCategories, final List<UserEntity> users) {
        final UserEntity admin = itemService.getUserService().findAdmin(users);
        for (final TransitiveItem transitiveItem : transitiveItemService.findAll()) {
            itemService.create(transitiveItem, infoCategories, users, admin);
        }
    }

    private void saveTransitiveItems(final List<TransitiveItem> items) {
        for (final TransitiveItem item : items) {
            transitiveItemService.getTransitiveItemRepository().save(item);
        }
    }

    private void createStubReplacers(final List<TransitiveItem> items) {
        for (final TransitiveItem item : items) {
            createStubReplacers(item);
        }
    }

    private void createStubReplacers(final TransitiveItem item) {
        final String category = item.getCategory();
        final Map<String, String> descriptionMap = ItemUtil.toMap(item.getDescription());
        final List<ReplacerData> replacersData = ItemUtil.parseReplacersSourceString(item.getReplacer());

        for (final ReplacerData replacerData : replacersData) {
            TransitiveItem replacer = transitiveItemService.find(category, replacerData.getName());
            if (replacer == null && category.equals("Rubber part")) {
                replacer = transitiveItemService.find("Bearing", replacerData.getName());
            }
            if (replacer == null) {
                final TransitiveItem stubReplacer = new TransitiveItem();
                stubReplacer.setName(replacerData.getName());
                stubReplacer.setReplacer("-");
                stubReplacer.setCategory(category);
                stubReplacer.setImage("-");
                if (category != null && category.equalsIgnoreCase("bearing")) {
                    BearingUtil.setBearingEnclosure(stubReplacer);
                }

                removeValues(descriptionMap, "Manufacturer", "Standard", "Material", "Screw class");

                stubReplacer.setDescription(ItemUtil.toDescription(descriptionMap));
                transitiveItemService.getTransitiveItemRepository().save(stubReplacer);
            }
        }
    }

    private void removeValues(final Map<String, String> descriptionMap, final String... keys) {
        for (final String key : keys) {
            if (descriptionMap.get(key) != null) {
                descriptionMap.put(key, "-");
            }
        }
    }

}
















