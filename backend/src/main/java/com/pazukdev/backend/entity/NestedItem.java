package com.pazukdev.backend.entity;

import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.entity.abstraction.Typeable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "child_item")
public class NestedItem extends AbstractEntity implements Typeable {

    public enum Type {
        PART, REPLACER
    }

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    private String comment = "-";
    private String quantity = "1";
    private String type;
    @Column(name = "creator_id")
    private Long creatorId;

    @Override
    public String getValuationType() {
        return type;
    }

    @Override
    public String toString() {
        return "type=" + type + ", comment=" + comment + ", quantity=" + quantity;
    }

}
