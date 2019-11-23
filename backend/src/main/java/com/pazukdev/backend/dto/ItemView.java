package com.pazukdev.backend.dto;

import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.PartsTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
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
public class ItemView extends AbstractDto {

    private boolean searchEnabled;
    private boolean newItem;
    private boolean addToWishList;
    private boolean hardDelete = false;
    private boolean defaultImg = true;
    private List<String> messages = new ArrayList<>();
    private NestedItemDto userData;
    private String imgData = "-";
    private RateReplacer rate;
    private String category;
    private HeaderTable header;
    private PartsTable partsTable;
    private ReplacersTable replacersTable;
    private Long itemId;
    private List<NestedItemDto> possibleParts = new ArrayList<>();
    private List<NestedItemDto> replacers = new ArrayList<>();
    private List<String> allCategories = new ArrayList<>();
    private Set<Long> idsToRemove = new HashSet<>();
    private Long creatorId;
    private String creatorName;
    private Set<Long> wishListIds = new HashSet<>();
    private Set<Long> ratedItems = new HashSet<>();
    private ItemView oldItemViewInEnglish;

}
