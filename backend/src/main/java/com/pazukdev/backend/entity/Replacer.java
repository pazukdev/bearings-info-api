package com.pazukdev.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "replacer")
public class Replacer extends AbstractEntity {

    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH})
//    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    private String comment = "-";

}
