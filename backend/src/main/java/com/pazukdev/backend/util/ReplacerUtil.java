package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.service.TransitiveItemService;

import java.util.*;

import static com.pazukdev.backend.util.NestedItemUtil.prepareNestedItemDtosToConverting;

public class ReplacerUtil {

    public static List<Replacer> createReplacers(final TransitiveItem transitiveItem,
                                                 final ItemService itemService,
                                                 final TransitiveItemService transitiveItemService) {
        final List<Replacer> replacers = new ArrayList<>();
        final String replacersSourceString = transitiveItem.getReplacer();
        if (replacersSourceString == null || replacersSourceString.equals("-")) {
            return replacers;
        }
        for (final String replacerData : Arrays.asList(replacersSourceString.split("; "))) {
            String replacerName;
            String comment = null;
            if (SpecificStringUtil.containsParentheses(replacerData)) {
                replacerName = SpecificStringUtil.getStringBeforeParentheses(replacerData);
                comment = SpecificStringUtil.getStringBetweenParentheses(replacerData);
            } else {
                replacerName = replacerData;
            }
            final String category = transitiveItem.getCategory();
            final TransitiveItem transitiveReplacerItem = transitiveItemService.find(category, replacerName);
            final Item replacerItem = itemService.getOrCreate(transitiveReplacerItem);

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
        final List<NestedItemDto> dtos = prepareNestedItemDtosToConverting(replacersTable.getReplacers());
        final String parentName = ChildItemUtil.getParentName(itemView, itemService);

        final Set<Replacer> replacersFromItemView = new HashSet<>();
        for (final NestedItemDto dto : dtos) {
            final Item replacerItem = itemService.getOne(dto.getItemId());

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
