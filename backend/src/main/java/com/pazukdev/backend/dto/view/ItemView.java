package com.pazukdev.backend.dto.view;

import com.pazukdev.backend.dto.LinkDto;
import com.pazukdev.backend.dto.NestedItemDto;
import com.pazukdev.backend.dto.UserActionDto;
import com.pazukdev.backend.dto.UserDto;
import com.pazukdev.backend.dto.table.HeaderTable;
import com.pazukdev.backend.dto.table.ReplacersTable;
import com.pazukdev.backend.entity.AdminMessage;
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
    private boolean hardDelete = true;
    private boolean partsEnabled = true;
    private boolean ordinaryItem;
    private String lang;
    private String itemId;
    private String category = "-";
    private String manufacturer = "-";
    private String vehicleClass;
    private String status;
    private String localizedCategory = "-";
    private String localizedName = "-";
    private UserDto userData;
    private UserDto creatorData;
    private HeaderTable header;
    private List<NestedItemDto> children = new ArrayList<>();
    private List<NestedItemDto> allChildren = new ArrayList<>();
    private ReplacersTable replacersTable;
    private List<NestedItemDto> possibleParts = new ArrayList<>();
    private List<NestedItemDto> possibleReplacers = new ArrayList<>();
    private List<String> allCategories = new ArrayList<>();
    private Set<Long> idsToRemove = new HashSet<>();
    private Set<Long> wishListIds = new HashSet<>();
    private Set<String> adminMessages = new HashSet<>();
    private String errorMessage = "-";
    private ItemView parents;
    private List<LinkDto> buyLinks = new ArrayList<>();
    private String wikiLink;
    private String websiteLink;
    private String manualLink;
    private String partsCatalogLink;
    private String drawingsLink;
    private String nameToRedirect;
    private double businessLogicTime;
    private double translationTime;
    private double responseTotalTime;
    private AdminMessage adminMessage;
    private List<UserActionDto> lastVehicles = new ArrayList<>();
    private List<UserActionDto> lastReplacers = new ArrayList<>();
    private int cachedViews;

}
