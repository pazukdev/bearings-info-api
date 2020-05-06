package com.pazukdev.backend.entity;

import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.entity.abstraction.Typeable;
import com.pazukdev.backend.util.LinkUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
import com.pazukdev.backend.util.UserActionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static com.pazukdev.backend.entity.factory.LinkFactory.LinkType;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "item")
@SecondaryTable(name = "item_description")
public class Item extends AbstractEntity implements Typeable {

    private String category;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "user_action_date")
    private String userActionDate;

    @Column(name = "description", table = "item_description")
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_child_item",
            joinColumns = @JoinColumn(name = "parent_item_id"),
            inverseJoinColumns = @JoinColumn(name = "child_item_id")
    )
    private Set<NestedItem> parts = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_replacer",
            joinColumns = @JoinColumn(name = "original_item_id"),
            inverseJoinColumns = @JoinColumn(name = "replacer_item_id")
    )
    private Set<NestedItem> replacers = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_link",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<Link> links = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_buy_link",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<Link> buyLinks = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany
    @JoinTable(
            name = "item_liked_user",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> likedUsers = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany
    @JoinTable(
            name = "item_disliked_user",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> dislikedUsers = new HashSet<>();

    public String getLink(final String linkType) {
        return LinkUtil.getLink(linkType, this);
    }

    public void setImg(final String img) {
        Link imgLink = getImgLink();
        if (SpecificStringUtil.isEmpty(img)) {
            links.remove(imgLink);
            return;
        }
        if (imgLink == null) {
            imgLink = new Link();
            imgLink.setType(LinkType.IMG);
            imgLink.setCountryCode("-");
        }
        imgLink.setUrl(img);
        links.add(imgLink);
    }

    private Link getImgLink() {
        return LinkUtil.getLink(LinkType.IMG, this.getLinks());
    }

    @Override
    public String toString() {
        return "item " + super.toString() + " category=" + category;
    }

    @Override
    public String getValuationType() {
        return UserActionUtil.ValuationType.ITEM;
    }
}
