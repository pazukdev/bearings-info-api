package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.UserItemStringReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.pazukdev.backend.util.SpecificStringUtil.replaceEmptyWithDash;

public class CSVUtil {

    protected final static Logger LOGGER = LoggerFactory.getLogger(UserItemStringReport.class);

    public static String getValue(final String key, final List<String> header, final List<String> userData) {
        final int i = header.indexOf(key);
        if (i < 0) {
            LOGGER.error("Header doesn't contain: " + key);
        }
        return replaceEmptyWithDash(userData.get(i));
    }

}
