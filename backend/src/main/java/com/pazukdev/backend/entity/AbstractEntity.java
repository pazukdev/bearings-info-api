package com.pazukdev.backend.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
//    @NotNull
    @Column(name = "name")
    private String name = "-";
    private String status = "active";

}
