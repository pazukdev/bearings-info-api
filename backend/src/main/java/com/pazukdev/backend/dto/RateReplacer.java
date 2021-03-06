package com.pazukdev.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class RateReplacer implements Serializable {

    private final static long serialVersionUID = 12343L;

    private String action;
    private Long itemId;
    private List<NestedItemDto> replacers;
    private int newUserRating;
    private int cachedViews;

    private List<Long> likedUserIds = new ArrayList<>();
    private List<Long> dislikedUserIds = new ArrayList<>();

}
