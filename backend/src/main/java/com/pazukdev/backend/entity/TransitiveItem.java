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
@Table(name = "transitive_item")
public class TransitiveItem extends AbstractEntity {

    private String category;
    private String image;
    private String description = "-";
    private String replacer = "-";
    private String wiki;
    private String website;
    private String manual;
    private String parts;
    private String drawings;
    @Column(name = "website_lang")
    private String websiteLang;

}
