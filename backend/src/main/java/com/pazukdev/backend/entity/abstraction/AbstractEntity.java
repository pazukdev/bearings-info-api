package com.pazukdev.backend.entity.abstraction;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@MappedSuperclass
@EqualsAndHashCode
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "name")
    protected String name = "-";

    protected String status = "active";

    @Override
    public String toString() {
        return "id=" + id + " name=" + name;
    }

}
