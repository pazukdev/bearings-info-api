package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.util.SpecificStringUtil.*;

public class ReplacerUtil {

    public static List<Replacer> createReplacers(final TransitiveItem transitiveItem,
                                                 final ItemService itemService,
                                                 final TransitiveItemService transitiveItemService,
                                                 final List<String> infoCategories,
                                                 final List<UserEntity> users,
                                                 final UserEntity admin) {
        final List<Replacer> replacers = new ArrayList<>();
        final String replacersSourceString = transitiveItem.getReplacer();
        if (isEmpty(replacersSourceString)) {
            return replacers;
        }
        if (replacersSourceString == null || replacersSourceString.equals("-")) {
            return replacers;
        }
        for (final String replacerData : replacersSourceString.split("; ")) {
            String replacerName;
            String comment = null;
            if (containsParentheses(replacerData)) {
                replacerName = getStringBeforeParentheses(replacerData);
                comment = getStringBetweenParentheses(replacerData);
            } else {
                replacerName = replacerData;
            }
            String category = transitiveItem.getCategory();
            TransitiveItem transitiveReplacerItem = transitiveItemService.find(category, replacerName);
            if (transitiveReplacerItem == null && category.equals("Rubber part")) {
                category = "Bearing";
                transitiveReplacerItem = transitiveItemService.find(category, replacerName);
            }
            final Item replacerItem = itemService.create(transitiveReplacerItem, infoCategories, users, admin);

            final Replacer replacer = new Replacer();
            replacer.setName(NestedItemUtil.createName(transitiveItem.getName(), replacerName));
            replacer.setItem(replacerItem);
            if (comment != null) {
                replacer.setComment(comment);
            }

            replacers.add(replacer);
        }

        return replacers;
    }

    public static Set<Replacer> createReplacersFromItemView(final ItemView itemView,
                                                            final ItemService itemService) {
        final ReplacersTable replacersTable = itemView.getReplacersTable();
//        final List<NestedItemDto> dtos = prepareNestedItemDtosToConverting(replacersTable.getReplacers());
        final List<NestedItemDto> dtos = replacersTable.getReplacers();
        final String parentName = ChildItemUtil.getParentName(itemView, itemService);

        final Set<Replacer> replacersFromItemView = new HashSet<>();
        for (final NestedItemDto dto : dtos) {
            final Item replacerItem = itemService.findOne(dto.getItemId());

            final Replacer replacer = new Replacer();
            replacer.setId(dto.getId());
            replacer.setName(ChildItemUtil.getName(parentName, replacerItem.getName()));
            replacer.setItem(replacerItem);
            replacer.setComment(dto.getComment());
            replacer.setStatus(dto.getStatus());

            replacersFromItemView.add(replacer);
        }

        return replacersFromItemView;
    }

}
