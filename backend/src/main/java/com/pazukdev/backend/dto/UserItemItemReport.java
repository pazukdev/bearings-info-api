package com.pazukdev.backend.dto;

import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.service.ItemService;
import com.pazukdev.backend.util.LoggerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

import static com.pazukdev.backend.util.CSVUtil.getValue;
import static com.pazukdev.backend.util.SpecificStringUtil.*;
import static com.pazukdev.backend.util.UserUtil.UserParam;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserItemItemReport extends UserItemReport<Item> {

    public static UserItemItemReport create(final List<String> header,
                                            final List<String> userData,
                                            final ItemService service) {
        final UserItemItemReport userItemReport = new UserItemItemReport();
        userItemReport.createdItems.addAll(getItems(getValue(UserParam.CREATED_ITEMS, header, userData), service));
        userItemReport.likedItems.addAll(getItems(getValue(UserParam.LIKED_ITEMS, header, userData), service));
        userItemReport.dislikedItems.addAll(getItems(getValue(UserParam.DISLIKED_ITEMS, header, userData), service));
        return userItemReport;
    }

    private static List<Item> getItems(final String source, final ItemService itemService) {
        final List<Item> items = new ArrayList<>();
        if (isEmpty(source)) {
            return items;
        }
        for (final String s : source.split(";")) {
            final String name = getStringBeforeParentheses(s);
            final String category = getStringBetweenParentheses(s);
            final Item item = itemService.findFirstByCategoryAndName(category, name);
            if (item == null) {
                LoggerUtil.error("Item name=" + name + " category=" + category + " not found");
                continue;
            }
            items.add(item);
        }
        return items;
    }

}
