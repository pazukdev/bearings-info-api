package com.pazukdev.backend.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public class CodeValidator {

    private static final Logger LOG = LoggerFactory.getLogger("CodeValidator");

    public static void main(String[] args) {
        System.out.println(Arrays.asList(Locale.getISOCountries()));
    }

    public static void validateCountryCode(final String countryCode) throws Exception {
        if (countryCode == null
                || countryCode.length() != 2
                || !Arrays.asList(Locale.getISOCountries()).contains(countryCode)) {
            final String message = "Invalid country code: " + countryCode;
            LOG.info(message);
            throw new Exception(message);
        }
    }

    public static void validateLangCode(final String lang) throws Exception {
        if (lang == null
                || lang.length() != 2
                || !Arrays.asList(Locale.getISOLanguages()).contains(lang.toLowerCase())) {
            final String message = "Invalid language code: " + lang;
            LOG.info(message);
            throw new Exception(message);
        }
    }

    public static boolean isLangCodeValid(final String lang) {
        try {
            validateLangCode(lang);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean isCountryCodeValid(final String lang) {
        try {
            validateCountryCode(lang);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

}
