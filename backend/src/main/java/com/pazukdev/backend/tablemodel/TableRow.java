package com.pazukdev.backend.tablemodel;

import com.pazukdev.backend.product.specification.Specification;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pazukdev.backend.util.SpecificStringUtil.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TableRow {

    private final Map<String, String> data;

    public static TableRow create() {
        return new TableRow(new HashMap<>());
    }

    public void put(String key, String value) {
        if (key != null) {
            key = key.trim();
        }
        if (value != null) {
            value = value.trim();
        }
        data.put(key, value);
    }

    public List<String> getStringValues(final Specification characteristic) {
        return getList(getStringValue(characteristic));
    }

    public String getStringValue(final Specification characteristic) {
        return getString(data.get(transformToKey(characteristic)));
    }

    public String getStringValueBeforeParenthesises(final Specification characteristic) {
        return getStringBeforeParentheses(getStringValue(characteristic));
    }

    public Integer getIntegerValue(final Specification characteristic) {
        return extractIntegerAutomatically(getStringValue(characteristic));
    }

    public Integer getProductionStartYear(final Specification characteristic) {
        return getIntegerBeforeDash(getStringValue(characteristic));
    }

    public Integer getProductionStopYear(final Specification characteristic) {
        return getIntegerAfterDash(getStringValue(characteristic));
    }

    private String transformToKey(final Specification characteristic) {
        return characteristic.toString().toLowerCase();
    }

}


















