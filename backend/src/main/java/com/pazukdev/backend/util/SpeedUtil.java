package com.pazukdev.backend.util;

import lombok.Getter;

/**
 * @author Siarhei Sviarkaltsau
 */
public class SpeedUtil {

    public static Integer calculateSpeed(final int engineRpm) {
        final double tireRadiusM = 484D / 2 / 1000;
        final double gearboxRatio = GearRatio.STANDARD_GEARBOX.getValue();
        final double finalDriveRatio = GearRatio.EIGHT_TEETH_MAIN_GEAR.getValue();
        return calculateSpeed(engineRpm, gearboxRatio, finalDriveRatio, tireRadiusM);
    }

    private static int calculateSpeed(final int engineRpm,
                                     final double gearboxRatio,
                                     final double finalDriveRatio,
                                     final int wheelRadiusInch,
                                     final double tireHeight) {
        double tireRadiusM = calculateTireRadiusInMeters(wheelRadiusInch, tireHeight);
        return calculateSpeed(engineRpm, gearboxRatio, finalDriveRatio, tireRadiusM);
    }

    private static Integer calculateSpeed(final int engineRpm,
                                     final double gearboxRatio,
                                     final double finalDriveRatio,
                                     final double tireRadiusM) {
        final double speedMs = (2 * Math.PI * tireRadiusM * engineRpm) / (60 * gearboxRatio * finalDriveRatio);
        final double msKmHRatio = 1000D / 3600D;
        final double speedKmH = speedMs / msKmHRatio;
        return (int) speedKmH;
    }

    private static Double calculateTireRadiusInMeters(final int wheelRadiusInch, final double tireHeight) {
        return Math.PI * (2.54 * wheelRadiusInch + tireHeight);
    }

    @Getter
    enum GearRatio {

        EIGHT_TEETH_MAIN_GEAR(4.62),
        ELEVEN_TEETH_MAIN_GEAR(3.89),
        TEN_TEETH_MAIN_GEAR(3.3),
        STANDARD_GEARBOX(1.3);

        private final double value;

        GearRatio(final double value) {
            this.value = value;
        }
    }

}
