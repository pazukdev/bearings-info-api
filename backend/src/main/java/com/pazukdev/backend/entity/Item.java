package com.pazukdev.backend.entity;

import com.pazukdev.backend.util.LinkUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
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
@SecondaryTable(name = "item_description")
public class Item extends AbstractEntity {

    private String category;
    private Integer rating = 0;
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
    private Set<ChildItem> childItems = new HashSet<>();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_replacer",
            joinColumns = @JoinColumn(name = "original_item_id"),
            inverseJoinColumns = @JoinColumn(name = "replacer_item_id")
    )
    private Set<Replacer> replacers = new HashSet<>();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "item_link",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<Link> links = new HashSet<>();

    public String getImg() {
        final Link link = LinkUtil.getLink("img", this);
        return link != null ? link.getName() : null;
    }

    public String getWiki() {
        final Link link = LinkUtil.getLink("wiki", this);
        return link != null ? link.getName() : null;
    }

    public void setImg(final String img) {
        Link imgLink = getImgLink();
        if (SpecificStringUtil.isEmpty(img)) {
            links.remove(imgLink);
            return;
        }
        if (imgLink == null) {
            imgLink = new Link();
            imgLink.setType("img");
        }
        imgLink.setName(img);
        links.add(imgLink);
    }

    private Link getImgLink() {
        return LinkUtil.getLink("img", this);
    }

}
