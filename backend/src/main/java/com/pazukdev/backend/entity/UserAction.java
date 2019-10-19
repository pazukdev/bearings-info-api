package com.pazukdev.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    private String userId = "-";
    private String actionDate = "-";
    private String actionType = "-";
    private String parentItemId = "-";
    private String itemId = "-";
    private String itemType = "-";
    private String itemCategory = "-";

}
