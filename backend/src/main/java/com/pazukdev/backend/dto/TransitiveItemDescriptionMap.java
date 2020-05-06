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
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> selectableParameters = new HashMap<>();
    private Map<String, String> items = new HashMap<>();

    public String getAndRemoveParam(final String key) {
        final String value = parameters.get(key);
        parameters.remove(key);
        return value;
    }

}
