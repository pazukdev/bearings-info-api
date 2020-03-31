package com.pazukdev.backend.tablemodel;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

}


















