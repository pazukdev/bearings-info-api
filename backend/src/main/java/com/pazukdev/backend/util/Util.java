package com.pazukdev.backend.util;

import java.util.Arrays;

public class Util {

    public static boolean containsNull(final Object... objects) {
        if (objects == null) {
            return true;
        }
        return Arrays.asList(objects).contains(null);
    }

}
