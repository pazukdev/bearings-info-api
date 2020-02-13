package com.pazukdev.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "user_action")
public class UserAction extends AbstractEntity {

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "action_date")
    private String actionDate = "-";
    @Column(name = "action_type")
    private String actionType = "-";
    @Column(name = "parent_item_id")
    private Long parentItemId;
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "item_type")
    private String itemType = "-";
    @Column(name = "item_category")
    private String itemCategory = "-";

}
