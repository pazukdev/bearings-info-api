package com.pazukdev.backend.dto.view;

import com.pazukdev.backend.dto.LikeListDto;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.dto.user.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ItemView extends AbstractView {

    private boolean searchEnabled;
    private boolean newItem;
    private boolean hardDelete;
    private boolean partsEnabled = true;
    private boolean ordinaryItem;
    private Long itemId;
    private String category = "-";
    private String localizedCategory = "-";
    private String localizedName = "-";
    private UserDto userData;
    private HeaderTable header;
    private PartsTable partsTable;
    private ReplacersTable replacersTable;
    private List<NestedItemDto> possibleParts = new ArrayList<>();
    private List<NestedItemDto> possibleReplacers = new ArrayList<>();
    private List<String> allCategories = new ArrayList<>();
    private Set<Long> idsToRemove = new HashSet<>();
    private Long creatorId;
    private String creatorName;
    private Set<Long> wishListIds = new HashSet<>();
    private LikeListDto likeList;
    private String wikiLink;
    private String websiteLink;
    private String websiteLang;
    private double businessLogicTime;
    private double translationTime;
    private double responseTotalTime;

}
