package com.pazukdev.backend.dto;

import com.pazukdev.backend.entity.TransitiveItem;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public class TransitiveItemDescriptionMap {

    private TransitiveItem parent;
    private Map<String, String> characteristics = new HashMap<>();
    private Map<String, String> selectableCharacteristics = new HashMap<>();
    private Map<String, String> items = new HashMap<>();

}
