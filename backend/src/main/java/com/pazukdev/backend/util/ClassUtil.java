package com.pazukdev.backend.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {

    public static List<String> getFieldsValues(final Class c) {
        List<String> values = new ArrayList<>();
        for (final Field field : c.getDeclaredFields()) {
            try {
                values.add(field.get(field.getType()).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

}
