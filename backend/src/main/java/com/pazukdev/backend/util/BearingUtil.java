package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.TransitiveItem;

import java.util.List;
import java.util.Map;

public class BearingUtil {

    public static void setBearingEnclosure(final TransitiveItem bearing) {
        final String category = bearing.getCategory();
        if (category == null || !category.toLowerCase().equals("bearing")) {
            return;
        }
        final String name = bearing.getName();
        final List<String> nameAsList = SpecificStringUtil.toList(name);
        if (nameAsList.size() >= 4) {
            final String last = nameAsList.get(nameAsList.size() - 1).toLowerCase();
            final String preLast = nameAsList.get(nameAsList.size() - 2).toLowerCase();
            final String prePreLast = nameAsList.get(nameAsList.size() - 3).toLowerCase();
            final String prePrePreLast = nameAsList.get(nameAsList.size() - 4).toLowerCase();
            final Map<String, String> description = ItemUtil.toMap(bearing.getDescription());
            String value = null;
            if (last.equals("z")) {
                final String enclosure = "sealed";
                if (preLast.equals("z") && prePreLast.equals("-")) {
                    value = enclosure;
                } else if (preLast.equals("-")) {
                    value = enclosure;
                }
            } else if (last.equals("s")) {
                final String enclosure = "shielded";
                if (preLast.equals("r") && prePreLast.equals("2") && prePrePreLast.equals("-")) {
                    value = enclosure;
                } else if (preLast.equals("r") && prePreLast.equals("-")) {
                    value = enclosure;
                }
            }

            if (value != null) {
                description.put("Enclosure", value);
            }
        }
    }

    public static boolean isBearingRollingElementSpecifiedAsParameter(final String bearingRollingElementValue) {
        return bearingRollingElementValue.contains("ball (")
                || bearingRollingElementValue.contains("roller (")
                || bearingRollingElementValue.contains("needle (");
    }

}
