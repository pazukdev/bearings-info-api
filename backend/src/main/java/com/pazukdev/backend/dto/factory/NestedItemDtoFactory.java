package com.pazukdev.backend.dto.factory;

import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.ChildItemUtil;
import com.pazukdev.backend.util.NestedItemUtil;
import com.pazukdev.backend.util.UserUtil;

import java.util.List;
import java.util.Map;

import static com.pazukdev.backend.util.CategoryUtil.*;
import static com.pazukdev.backend.util.ItemUtil.createButtonText;
import static com.pazukdev.backend.util.ItemUtil.toMap;
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
        vehicleDto.setSecondComment(vehicle.getLink("img"));
        vehicleDto.setItemCategory(map.get(Category.MANUFACTURER));
        vehicleDto.setDeletable(false);
        vehicleDto.setVehicleClass(map.get(Parameter.CLASS));

        return vehicleDto;
    }

    public static NestedItemDto createChild(final NestedItem nestedItem,
                                            final UserService userService,
                                            final boolean addLocation) {
        final Item item = nestedItem.getItem();

        final NestedItemDto childItemDto = createBasicNestedItemDto(item, userService);
        childItemDto.setId(nestedItem.getId());
        childItemDto.setName(nestedItem.getName());
        if (addLocation) {
            childItemDto.setComment(nestedItem.getComment());
        }
        childItemDto.setSecondComment(nestedItem.getQuantity());
        childItemDto.setStatus(nestedItem.getStatus());
        childItemDto.setType(nestedItem.getType());
        return childItemDto;
    }

    public static NestedItemDto createWishListItem(final NestedItem nestedItem, final UserService userService) {
        final Item item = nestedItem.getItem();

        final NestedItemDto dto = createBasicNestedItemDto(item, userService);
        dto.setId(nestedItem.getId());
        dto.setName(ChildItemUtil.createNameForWishListItem(item.getName()));
        dto.setComment(nestedItem.getComment());
        dto.setSecondComment(nestedItem.getQuantity());
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

    public static NestedItemDto createBasicNestedItemDto(final Item item,
                                                         final UserService userService) {
        final Map<String, String> descriptionMap = toMap(item.getDescription());
        final String manufacturer = descriptionMap.get(Category.MANUFACTURER);
        final String partNumber = descriptionMap.get("Part number");
        final UserDto creator = UserUtil.getCreator(item, userService);
        final String creatorName = creator != null ? creator.getName() : "deleted user";

        final NestedItemDto dto = new NestedItemDto();
        dto.setName(" - " + item.getName());
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setItemCategory(item.getCategory());
        dto.setLikedUsers(NestedItemUtil.getLikedUserDtos(item.getLikedUsers()));
        dto.setDislikedUsers(NestedItemUtil.getLikedUserDtos(item.getDislikedUsers()));
        dto.setButtonText(createButtonText(item, manufacturer, partNumber));
        if (item.getCategory().equals(Category.SEAL)) {
            dto.setSize(descriptionMap.get(Parameter.SIZE));
        }
        dto.setManufacturer(manufacturer);
        dto.setStatus(item.getStatus());
        dto.setCreatorName(creatorName);
        dto.setCreatorId(item.getCreatorId());
        return dto;
    }

}
