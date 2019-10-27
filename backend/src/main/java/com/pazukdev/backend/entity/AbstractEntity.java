package com.pazukdev.backend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="judgements_id_seq")
    @SequenceGenerator(name="judgements_id_seq", sequenceName="judgements_id_seq", allocationSize=1)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "name")
    private String name = "-";
    private String status = "active";

}
