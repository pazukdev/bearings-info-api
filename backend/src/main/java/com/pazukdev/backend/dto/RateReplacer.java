package com.pazukdev.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class RateReplacer implements Serializable {

    private final static long serialVersionUID = 12343L;

    private String action;
    private Long itemId;
    private LikeListDto likeList;
    private List<NestedItemDto> replacers;
    private int newUserRating;

}
