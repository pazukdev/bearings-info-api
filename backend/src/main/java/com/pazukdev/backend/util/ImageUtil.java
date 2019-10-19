package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.Item;

public class ImageUtil {

    public static String getImage(final Item item) {
        if (item.getImage() != null) {
            return item.getCategory().toLowerCase() + "/" + item.getImage();
        } else if (item.getCategory().equals("Motorcycle")) {
            return "motorcycle/motorcycle_default.png";
        } else {
            return "common/default_image_small.png";
        }
    }

}
