package com.pazukdev.backend.dto;

import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.NestedItem;
import com.pazukdev.backend.service.UserService;
import com.pazukdev.backend.util.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pazukdev.backend.util.CategoryUtil.getItemsManagementComment;
import static com.pazukdev.backend.util.ItemUtil.createButtonText;
import static com.pazukdev.backend.util.ItemUtil.toMap;
import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data(staticConstructor = "create")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NestedItemDto extends AbstractDto {

    private Long itemId;
    private String itemName = "-";
    private String itemCategory = "-";
    private String manufacturer = "-";
    private String size;
    private String creatorName;
    private Long creatorId;
    private Integer rating = 0;
    private String type;

    private Set<NestedItemDto> likedUsers = new HashSet<>();
    private Set<NestedItemDto> dislikedUsers = new HashSet<>();

    private String buttonText = "-";

    private String comment = "-";
    private String secondComment = "-";

    private String vehicleIcon;
    private String vehicleClass;

    public void translateToLang(final String langTo, final List<String> dictionary) {
        final String langFrom = "en";
        final boolean name = false;
        final boolean addToDictionary = false;

        itemCategory = TranslatorUtil.translate(langFrom, langTo, itemCategory, name, addToDictionary, dictionary);
        buttonText = TranslatorUtil.translate(langFrom, langTo, buttonText, name, addToDictionary, dictionary);
        comment = TranslatorUtil.translate(langFrom, langTo, comment, name, addToDictionary, dictionary);
        secondComment = TranslatorUtil.translate(langFrom, langTo, secondComment, name, addToDictionary, dictionary);
        manufacturer = TranslatorUtil.translate(langFrom, langTo, manufacturer, name, addToDictionary, dictionary);
    }

    public static NestedItemDto createVehicle(final Item vehicle, final UserService service) {
        final String description = vehicle.getDescription();
        final Map<String, String> map = toMap(description);

        final NestedItemDto vehicleDto = createPart(vehicle, service);
        vehicleDto.setComment(map.get(CategoryUtil.Parameter.PRODUCTION));
        vehicleDto.setSecondComment(vehicle.getLink("img"));
        vehicleDto.setItemCategory(map.get(CategoryUtil.Category.MANUFACTURER));
        vehicleDto.setDeletable(false);
        vehicleDto.setVehicleClass(map.get(CategoryUtil.Parameter.CLASS));

        return vehicleDto;
    }

    public static NestedItemDto createWishListItem(final Item child,
                                                   final String comment,
                                                   final String quantity,
                                                   final Long userId,
                                                   final UserService service) {
        final NestedItemDto dto = createPart(child, service);
        dto.setName(ChildItemUtil.createNameForWishListItem(child.getName()));
        dto.setComment(comment);
        dto.setSecondComment(quantity);
        dto.setType(NestedItem.Type.WISHLIST_ITEM.name().toLowerCase());
        dto.setCreatorId(userId);
        return dto;
    }

    public static NestedItemDto createItemForItemsManagement(final Item child,
                                                             final UserService service,
                                                             final List<String> comments) {
        final NestedItemDto basicSpecialNestedItemDto = createPart(child, service);
        String leftColumnData = getItemsManagementComment(child, comments);
        basicSpecialNestedItemDto.setComment(!isEmpty(leftColumnData) ? leftColumnData : "-");
        return basicSpecialNestedItemDto;
    }

    public static NestedItemDto createPart(final Item child, final UserService service) {
        return NestedItemDto.create(child, NestedItem.Type.PART, service);
    }

    public static NestedItemDto createReplacer(final Item child, final UserService service) {
        return NestedItemDto.create(child, NestedItem.Type.REPLACER, service);
    }

    public static NestedItemDto create(final Item child,
                                       final NestedItem.Type type,
                                       final UserService service) {
        final Map<String, String> descriptionMap = toMap(child.getDescription());
        final String manufacturer = descriptionMap.get(CategoryUtil.Category.MANUFACTURER);
        final String partNumber = descriptionMap.get("Part number");
        final UserDto creator = UserUtil.getCreator(child, service);
        final String creatorName = creator != null ? creator.getName() : "deleted user";

        final NestedItemDto dto = new NestedItemDto();
        dto.setName(" - " + child.getName());
        dto.setItemId(child.getId());
        dto.setItemName(child.getName());
        dto.setItemCategory(child.getCategory());
        dto.setLikedUsers(NestedItemUtil.getLikedUserDtos(child.getLikedUsers()));
        dto.setDislikedUsers(NestedItemUtil.getLikedUserDtos(child.getDislikedUsers()));
        dto.setButtonText(createButtonText(child, manufacturer, partNumber));
        if (child.getCategory().equals(CategoryUtil.Category.SEAL)) {
            dto.setSize(descriptionMap.get(CategoryUtil.Parameter.SIZE));
        }
        dto.setManufacturer(manufacturer);
        dto.setStatus(child.getStatus());
        dto.setCreatorName(creatorName);
        dto.setCreatorId(child.getCreatorId());
        dto.setType(type.name().toLowerCase());

        return dto;
    }

}
