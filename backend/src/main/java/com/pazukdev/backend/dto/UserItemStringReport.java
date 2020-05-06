package com.pazukdev.backend.dto;

import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import static com.pazukdev.backend.util.UserUtil.isAdmin;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserItemStringReport extends UserItemReport<String> {

    public static UserItemStringReport create(final UserEntity user, final List<Item> items) {
        final UserItemStringReport userItemReport = new UserItemStringReport();
        for (final Item item : items) {
            final String itemInfo = item.getName() + " " + "(" + item.getCategory() + ")";

            if (item.getCreatorId().equals(user.getId()) && !isAdmin(user)) {
                userItemReport.createdItems.add(itemInfo);
            }
            if (item.getLikedUsers().contains(user)) {
                userItemReport.likedItems.add(itemInfo);
            }
            if (item.getDislikedUsers().contains(user)) {
                userItemReport.dislikedItems.add(itemInfo);
            }
        }
        return userItemReport;
    }

}
