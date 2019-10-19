package com.pazukdev.backend.util;

/**
 * @author Siarhei Sviarkaltsau
 */
public class FuelUtil {

    public static Integer getOperationalRange(final double fuelCapacityL,
                                              final double fuelConsumptionLPer100Km) {
        final double operationalRangeKm = fuelCapacityL * fuelConsumptionLPer100Km;
        return (int) operationalRangeKm;
    }

}
