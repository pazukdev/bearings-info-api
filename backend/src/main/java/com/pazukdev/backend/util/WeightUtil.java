package com.pazukdev.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Siarhei Sviarkaltsau
 */
public class WeightUtil {

    private final static int SCALE = 2;
    private final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal toKg(final Integer g) {
        return BigDecimal.valueOf(g).divide(BigDecimal.valueOf(1000), SCALE, ROUNDING_MODE);
    }

    public static Integer toG(final BigDecimal kg) {
        return kg.multiply(new BigDecimal(1000)).intValueExact();
    }

    public static Integer toG(final Integer kg) {
        return toG(new BigDecimal(kg));
    }

}
