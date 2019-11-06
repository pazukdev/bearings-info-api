package com.pazukdev.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "likelist")
public class LikeList extends AbstractEntity {

    @OneToMany
    @JoinTable(
            name = "likelist_item",
            joinColumns = @JoinColumn(name = "likelist_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> likedItems = new HashSet<>();
    @OneToMany
    @JoinTable(
            name = "dislikelist_item",
            joinColumns = @JoinColumn(name = "likelist_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> dislikedItems = new HashSet<>();

}
