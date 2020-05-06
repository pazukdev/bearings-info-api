package com.pazukdev.backend.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserItemReport<T> {

    protected Set<T> createdItems = new HashSet<>();
    protected Set<T> likedItems = new HashSet<>();
    protected Set<T> dislikedItems = new HashSet<>();

}
