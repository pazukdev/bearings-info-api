package com.pazukdev.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class LikeListDto implements Serializable {

    private final static long serialVersionUID = 12343L;

    private Set<Long> likedItemsIds = new HashSet<>();
    private Set<Long> dislikedItemsIds = new HashSet<>();

}
