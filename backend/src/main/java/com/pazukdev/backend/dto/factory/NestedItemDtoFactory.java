package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.user.UserDto;
import com.pazukdev.backend.entity.ChildItem;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Replacer;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.UserUtil;

import java.util.List;
import java.util.Map;

import static com.pazukdev.backend.util.CategoryUtil.*;
import static com.pazukdev.backend.util.ItemUtil.*;
import static com.pazukdev.backend.util.SpecificStringUtil.capitalize;
import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;

/**
 * @author Siarhei Sviarkaltsau
 */
public class NestedItemDtoFactory {

    public static NestedItemDto createUser(final UserEntity user) {
        final String role = capitalize(user.getRole().name());

        final NestedItemDto userData = new NestedItemDto();
        userData.setItemId(user.getId());
        userData.setItemName(user.getName());
        userData.setButtonText(user.getName());
        userData.setRating(user.getRating());
        userData.setItemCategory(user.getStatus());
        userData.setComment(role);
        userData.setSecondComment(user.getRating().toString());
        userData.setStatus(user.getStatus());
        return userData;
    }

    public static NestedItemDto createVehicle(final Item vehicle, final UserService userService) {
        final String description = vehicle.getDescription();
        final Map<String, String> map = toMap(description);

        final NestedItemDto vehicleDto = createBasicNestedItemDto(vehicle, userService);
        vehicleDto.setComment(map.get(Parameter.PRODUCTION));
        vehicleDto.setSecondComment(vehicle.getImg());
        vehicleDto.setItemCategory(map.get(Category.MANUFACTURER));
        vehicleDto.setDeletable(false);
        vehicleDto.setVehicleIcon(vehicle.getImg());
        vehicleDto.setVehicleClass(map.get(Parameter.CLASS));

        return vehicleDto;
    }

    public static NestedItemDto createChildItem(final ChildItem childItem,
                                                final UserService userService,
                                                final boolean addLocation) {
        final Item item = childItem.getItem();

        final NestedItemDto childItemDto = createBasicNestedItemDto(item, userService);
        childItemDto.setId(childItem.getId());
        childItemDto.setName(childItem.getName());
        if (addLocation) {
            childItemDto.setComment(childItem.getLocation());
        }
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

    public static NestedItemDto createItemForItemsManagement(final Item item,
                                                             final UserService userService,
                                                             final List<String> comments) {
        final NestedItemDto basicSpecialNestedItemDto = createBasicNestedItemDto(item, userService);
        String leftColumnData = getItemsManagementComment(item, comments);
        basicSpecialNestedItemDto.setComment(!isEmpty(leftColumnData) ? leftColumnData : "-");
        return basicSpecialNestedItemDto;
    }

    public static NestedItemDto createBasicNestedItemDto(final Item item, final UserService userService) {
        final Map<String, String> descriptionMap = toMap(item.getDescription());
        final String manufacturer = descriptionMap.get(Category.MANUFACTURER);
        final UserDto creator = UserUtil.getCreatorData(item, userService);
        final String creatorName = creator != null ? creator.getName() : "deleted user";

        final NestedItemDto dto = new NestedItemDto();
        dto.setName(" - " + item.getName());
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setItemCategory(item.getCategory());
        dto.setRating(item.getRating());
        dto.setButtonText(createButtonText(item, manufacturer));
        dto.setSelectText(createSelectText(item, manufacturer, descriptionMap));
        dto.setStatus(item.getStatus());
        dto.setCreatorName(creatorName);
        dto.setCreatorId(item.getCreatorId());
        return dto;
    }

}
