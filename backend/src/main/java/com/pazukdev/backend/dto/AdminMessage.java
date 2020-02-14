package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Siarhei Sviarkaltsau
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminMessage extends Message {

    private String link;
    private String linkText;

}
