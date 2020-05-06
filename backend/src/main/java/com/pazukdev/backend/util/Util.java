package com.pazukdev.backend.util;

import java.util.Arrays;

import static com.pazukdev.backend.util.SpecificStringUtil.getSomeBytes1;
import static com.pazukdev.backend.util.TranslatorUtil.getSomeBytes0;

/**
 * @author Siarhei Sviarkaltsau
 */
public class Util {

    public static boolean containsNull(final Object... objects) {
        if (objects == null) {
            return true;
        }
        return Arrays.asList(objects).contains(null);
    }

    public static String[] getMailSenderData() {
        return new String[]{new String(getSomeBytes0()), new String(getSomeBytes1())};
    }

}
