package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode
public class LinkDto implements Serializable {

    private final static long serialVersionUID = 12343L;
    @EqualsAndHashCode.Exclude
    private Long id;
    @EqualsAndHashCode.Exclude
    private String url;
    private String countryCode;

}
