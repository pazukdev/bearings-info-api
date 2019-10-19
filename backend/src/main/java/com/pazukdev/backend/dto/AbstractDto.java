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
    private String status = "created";

}
