package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ItemUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
import com.pazukdev.backend.util.UserUtil;

import static com.pazukdev.backend.util.ItemUtil.getValueFromDescription;

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
        userData.setQuantity(user.getRating().toString());
        userData.setStatus(user.getStatus());
        return userData;
    }

    public static NestedItemDto createMotorcycle(final Item motorcycle, final UserService userService) {
        final String description = motorcycle.getDescription();
        final String production = getValueFromDescription(description, "Production");
        final String manufacturer = getValueFromDescription(description, "Manufacturer").toUpperCase();

        final NestedItemDto motorcycleDto = createBasicNestedItemDto(motorcycle, userService);
        motorcycleDto.setLocation(production);
        motorcycleDto.setComment(manufacturer);
        motorcycleDto.setItemCategory(manufacturer);
        return motorcycleDto;
    }

    public static NestedItemDto createChildItem(final ChildItem childItem, final UserService userService) {
        final Item item = childItem.getItem();

        final NestedItemDto childItemDto = createBasicNestedItemDto(item, userService);
        childItemDto.setId(childItem.getId());
        childItemDto.setName(childItem.getName());
        childItemDto.setQuantity(childItem.getQuantity());
        childItemDto.setLocation(childItem.getLocation());
        childItemDto.setStatus(childItem.getStatus());
        return childItemDto;
    }

    public static NestedItemDto createReplacer(final Replacer replacer, final UserService userService) {
        final Item item = replacer.getItem();

        final NestedItemDto replacerDto = createBasicNestedItemDto(item, userService);
        replacerDto.setId(replacer.getId());
        replacerDto.setName(replacer.getName());
        replacerDto.setComment(replacer.getComment());
        replacerDto.setQuantity("-");
        replacerDto.setLocation("-");
        return replacerDto;
    }

    public static NestedItemDto createBasicSpecialNestedItemDto(final Item item, final UserService userService) {
        final NestedItemDto basicSpecialNestedItemDto = createBasicNestedItemDto(item, userService);
        basicSpecialNestedItemDto.setLocation(item.getCategory());
        return basicSpecialNestedItemDto;
    }

    public static NestedItemDto createBasicNestedItemDto(final Item item, final UserService userService) {
        final String name = " - " + item.getName();
        final Long itemId = item.getId();
        final String itemName = item.getName();
        final String buttonText = ItemUtil.createButtonText(item);
        final String selectText = ItemUtil.createSelectText(item);

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
