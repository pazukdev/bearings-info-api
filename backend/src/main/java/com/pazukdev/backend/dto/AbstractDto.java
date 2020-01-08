package com.pazukdev.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class AbstractDto implements Serializable {

    private final static long serialVersionUID = 12343L;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name = "-";
    private String localizedName = "-";
    private String status = "created";
    private boolean deletable = true;

    public void setName(final String name) {
        this.name = name;
        this.localizedName = name;
    }
}
