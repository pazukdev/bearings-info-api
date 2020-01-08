package com.pazukdev.backend.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextData {

    public static final String NAME = "name";
    public static final String FULL_NAME = "full name";
    public static final String MANUFACTURER = "manufacturer";
    public static final String PRODUCTION = "production";
    public static final String COUNTRY = "country";
    public static final String FOUNDED = "founded";
    public static final String DEFUNCT = "defunct";
    public static final String CORE = "core";
    public static final String INSULATION = "insulation";
    public static final String OUTER_SHIELD_MATERIAL = "outer shield material";
    public static final String TYPE = "type";
    public static final String VOLTAGE = "voltage";

    private static final List<String> fixedParams = Arrays
            .asList(NAME, FULL_NAME, PRODUCTION, MANUFACTURER, COUNTRY, FOUNDED, DEFUNCT);

    private static final List<String> translatableSubstrings = Arrays
            .asList("gost", "imz", "kmz");

    private static final List<String> descriptionIgnore = Arrays
            .asList(NAME, "category", "replacer", "image", "website", "website lang", "wiki");

    private static final Map<String, Integer> parametersWeight = new HashMap<String, Integer>() {{
        put(NAME, 100);
        put(FULL_NAME, 99);

        put(TYPE, 97);

        put(MANUFACTURER, 80);
        put(COUNTRY, 80);

        put(FOUNDED, 50);
        put(DEFUNCT, 49);

        put(VOLTAGE, 40);

        put(CORE, 30);
        put(INSULATION, 29);
        put(OUTER_SHIELD_MATERIAL, 28);
    }};

    public static boolean isFixed(final String parameter) {
        return fixedParams.contains(parameter.toLowerCase());
    }

    public static Integer getWeight(final String parameter) {
        final Integer weight = parametersWeight.get(parameter.toLowerCase());
        return weight != null ? weight : 0;
    }

    public static boolean isDescriptionIgnored(final String parameter) {
        return descriptionIgnore.contains(parameter.toLowerCase());
    }

    public static boolean isTranslatableSubstring(final String substring) {
        return translatableSubstrings.contains(substring.toLowerCase());
    }

}
