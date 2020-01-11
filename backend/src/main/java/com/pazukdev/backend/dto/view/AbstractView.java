package com.pazukdev.backend.dto.view;

import com.pazukdev.backend.dto.AbstractDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractView extends AbstractDto {

    protected String img;
    protected String defaultImg;
    protected List<String> messages = new ArrayList<>();

}
