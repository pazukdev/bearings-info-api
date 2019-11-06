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
@Table(name = "item")
public class Item extends AbstractEntity {

    private String category;
    private Integer rating = 0;
    @Column(name = "creator_id")
    private Long creatorId;
    @Column(name = "user_action_date")
    private String userActionDate;
    private String description;
    private String image;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "item_child_item",
            joinColumns = @JoinColumn(name = "parent_item_id"),
            inverseJoinColumns = @JoinColumn(name = "child_item_id")
    )
    private Set<ChildItem> childItems = new HashSet<>();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    //@Getter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "item_replacer",
            joinColumns = @JoinColumn(name = "original_item_id"),
            inverseJoinColumns = @JoinColumn(name = "replacer_item_id")
    )
    private Set<Replacer> replacers = new HashSet<>();

}
