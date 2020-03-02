package com.pazukdev.backend.dataloader;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.dto.ReplacerData;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.entity.factory.TransitiveItemFactory;
import com.pazukdev.backend.repository.TransitiveItemRepository;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;
import com.pazukdev.backend.util.BearingUtil;
import com.pazukdev.backend.util.ItemUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.pazukdev.backend.util.FileUtil.FileName;
import static com.pazukdev.backend.util.FileUtil.getTxtFileTextLines;

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

    private void populateEmptyTables() {
        if (!repositoryIsEmpty(transitiveItemService.getTransitiveItemRepository())) {
            return;
        }
        final long start = System.nanoTime();
        final String startTime = LocalTime.now().toString();

        final List<String> infoCategories = getTxtFileTextLines(FileName.INFO_CATEGORIES);

        createDefaultUsers();
        createTransitiveItems();
        createItems(infoCategories);

        final long stop = System.nanoTime();
//        LOG.info("Start time: " + startTime);
//        LOG.info("Stop time: " + LocalTime.now());
        final Double time = (stop - start) * 0.000000001;
        LOG.info("DB created in " + time.intValue() + " seconds");
    }

    private boolean repositoryIsEmpty(final TransitiveItemRepository repository) {
        return repository.findAll().isEmpty();
    }

    private void createDefaultUsers() {
        createUser(Role.GUEST, "guest", "$2a$10$unchbvwqbdJHEaRU/zT03emzPvORNIDnVYXgWUh8tN8G2WlcnPH6y");
        createUser(Role.ADMIN, "admin", "$2a$10$LJDm6BOaekdsan3q3j15Q.ceRCSHHb1J8kAPqQasWZSdKoJtDAnyO");
        createUser(Role.ADMIN, "dominator", "$2a$10$mRsNu6BVh3YAm1vKWwsbz.AlOUqzoi0eW9TAcV5AysIciUyusnxrm");
        createUser(Role.USER, "user", "$2a$10$50E.w9jZJAIjGlsb4OU0N.wSvxrfWe.VEmiAV7.filaKuuKN.f992");
        createUser(Role.SELLER, "soyuz retromechanic", "$2a$10$50E.w9jZJAIjGlsb4OU0N.wSvxrfWe.VEmiAV7.filaKuuKN.f992");
    }

    private void createUser(final Role role, final String name, final String password) {
        final UserEntity user = new UserEntity();
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);
        if (name.equalsIgnoreCase("admin")) {
            user.setEmail("pazukdev@gmail.com");
            user.setImg("https://drive.google.com/open?id=1iuDdjYXtphxQ8UlQD-jjWYnEb_JC9OcG");
            user.setCountry("ZW");
        } else if (name.equalsIgnoreCase("dominator")) {
            user.setCountry("MC");
            user.setImg("https://drive.google.com/open?id=1YUwn8rh8ZNjg4sw_MPVccY936D87fman");
        } else if (name.equalsIgnoreCase("soyuz retromechanic")) {
            user.setCountry("BY");
            user.setImg("https://drive.google.com/open?id=1rtWwJf6XvEp2EV8p79ok5_NwlsOwawVC");
        }
        itemService.getUserService().getRepository().save(user);
    }

    private void createTransitiveItems() {
        final List<TransitiveItem> transitiveItems = transitiveItemFactory.createEntitiesFromCSVFile();
        saveTransitiveItems(transitiveItems);
        createStubReplacers(transitiveItems);
    }

    private void createItems(final List<String> infoCategories) {
        for (final TransitiveItem transitiveItem : transitiveItemService.findAll()) {
            itemService.create(transitiveItem, infoCategories);
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
















