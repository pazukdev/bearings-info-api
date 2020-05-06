package com.pazukdev.backend.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@Entity
@Table(name = "user_action")
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "action_date")
    private String actionDate = "-";
    @Column(name = "action_type")
    private String actionType = "-";
    @Column(name = "parent_id")
    private Long parentId = 0L;
    @Column(name = "item_id")
    private Long itemId;
//    @Column(name = "item_type")
//    private String itemType = "-";
//    @Column(name = "item_category")
//    private String itemCategory = "-";
    private String note = "-";
    private String message = "-";

}
