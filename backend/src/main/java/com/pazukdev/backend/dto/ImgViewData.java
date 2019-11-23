package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImgViewData extends AbstractDto {

    private final boolean defaultImg;
    private final String imgData;

}
