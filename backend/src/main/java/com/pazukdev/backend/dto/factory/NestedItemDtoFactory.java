package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
import com.pazukdev.backend.util.UserUtil;

import static com.pazukdev.backend.util.ItemUtil.*;

/**
 * @author Siarhei Sviarkaltsau
 */
public class NestedItemDtoFactory {

    public static NestedItemDto createUser(final UserEntity user) {
        final String role = SpecificStringUtil.capitalize(user.getRole().name());

        final NestedItemDto userData = new NestedItemDto();
        userData.setItemId(user.getId());
        userData.setItemName(user.getName());
        userData.setButtonText(user.getName());
        userData.setRating(user.getRating());
        userData.setItemCategory(role);
        userData.setComment(role);
        userData.setSecondComment(user.getRating().toString());
        userData.setStatus(user.getStatus());
        return userData;
    }

    public static NestedItemDto createMotorcycle(final Item motorcycle, final UserService userService) {
        final String description = motorcycle.getDescription();
        final String production = getValueFromDescription(description, "Production");
        final String manufacturer = getValueFromDescription(description, "Manufacturer");

        final NestedItemDto motorcycleDto = createBasicNestedItemDto(motorcycle, userService);
        motorcycleDto.setComment(production);
        motorcycleDto.setSecondComment(manufacturer);
        motorcycleDto.setItemCategory(manufacturer);
        motorcycleDto.setDeletable(false);
        return motorcycleDto;
    }

    public static NestedItemDto createChildItem(final ChildItem childItem, final UserService userService) {
        final Item item = childItem.getItem();

        final NestedItemDto childItemDto = createBasicNestedItemDto(item, userService);
        childItemDto.setId(childItem.getId());
        childItemDto.setName(childItem.getName());
        childItemDto.setComment(childItem.getLocation());
        childItemDto.setSecondComment(childItem.getQuantity());
        childItemDto.setStatus(childItem.getStatus());
        return childItemDto;
    }

    public static NestedItemDto createReplacer(final Replacer replacer, final UserService userService) {
        final Item item = replacer.getItem();

        final NestedItemDto replacerDto = createBasicNestedItemDto(item, userService);
        replacerDto.setId(replacer.getId());
        replacerDto.setName(replacer.getName());
        replacerDto.setComment(replacer.getComment());
        replacerDto.setSecondComment("-");
        return replacerDto;
    }

    public static NestedItemDto createWishListItem(final ChildItem childItem, final UserService userService) {
        final Item item = childItem.getItem();

        final NestedItemDto dto = createBasicNestedItemDto(item, userService);
        dto.setId(childItem.getId());
        dto.setName(ChildItemUtil.createNameForWishListItem(item.getName()));
        dto.setComment(childItem.getLocation());
        dto.setSecondComment(childItem.getQuantity());
        return dto;
    }

    public static NestedItemDto createItemForItemsManagement(final Item item, final UserService userService) {
        final NestedItemDto basicSpecialNestedItemDto = createBasicNestedItemDto(item, userService);
        final String category = item.getCategory();
        final String description = item.getDescription();

        String leftColumnData = null;

        if (category.toLowerCase().equals("bearing")) {
            leftColumnData = getValueFromDescription(description, "Type");
        } else if (category.toLowerCase().equals("seal")) {
            leftColumnData = getValueFromDescription(description, "Size, mm");
        } else if (category.toLowerCase().equals("oil")) {
            leftColumnData = getValueFromDescription(description, "Base");
        } else if (category.toLowerCase().equals("motorcycle")) {
            leftColumnData = getValueFromDescription(description, "Manufacturer");
        } else if (category.toLowerCase().equals("spark plug")) {
            leftColumnData = getValueFromDescription(description, "Manufacturer");
        } else if (category.toLowerCase().equals("material")) {
            leftColumnData = getValueFromDescription(description, "Type");
        } else if (category.toLowerCase().equals("wire")) {
            leftColumnData = getValueFromDescription(description, "Voltage");
        } else if (category.toLowerCase().equals("generator")) {
            leftColumnData = getValueFromDescription(description, "Tension, V");
        } else if (category.toLowerCase().equals("standard")) {
            leftColumnData = getValueFromDescription(description, "Full name");
        } else if (category.toLowerCase().equals("universal joint")) {
            leftColumnData = getValueFromDescription(description, "Full name");
        } else if (category.toLowerCase().equals("manufacturer")) {
            leftColumnData = getValueFromDescription(description, "Country");
        }

        basicSpecialNestedItemDto.setComment(leftColumnData != null ? leftColumnData : "-");

        return basicSpecialNestedItemDto;
    }

    public static NestedItemDto createBasicSpecialNestedItemDto(final Item item, final UserService userService) {
        final NestedItemDto basicSpecialNestedItemDto = createBasicNestedItemDto(item, userService);
        basicSpecialNestedItemDto.setComment(item.getCategory());
        return basicSpecialNestedItemDto;
    }

    public static NestedItemDto createBasicNestedItemDto(final Item item, final UserService userService) {
        final String name = " - " + item.getName();
        final Long itemId = item.getId();
        final String itemName = item.getName();
        final String buttonText = createButtonText(item);
        final String selectText = createSelectText(item);

        final NestedItemDto nestedItemDto = new NestedItemDto();
        nestedItemDto.setName(name);
        nestedItemDto.setItemId(itemId);
        nestedItemDto.setItemName(itemName);
        nestedItemDto.setItemCategory(item.getCategory());
        nestedItemDto.setRating(item.getRating());
        nestedItemDto.setButtonText(buttonText);
        nestedItemDto.setSelectText(selectText);
        nestedItemDto.setStatus(item.getStatus());
        nestedItemDto.setCreatorName(UserUtil.getCreatorName(item, userService));
        return nestedItemDto;
    }

}
