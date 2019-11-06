package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.Item;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImgUtil {

    public static final String IMG_DIRECTORY_PATH = "backend/src/img/";
    private static final String PNG_EXTENSION = "png";
    private static final String IMG_DATA_METADATA = "data:image/png;base64,";

    public static String getItemImgData(final Item item) {
        final String itemCategory = item.getCategory();
        String imgName;
        String imgPath;
        BufferedImage img;
        if (item.getImage() != null) {
            imgName = item.getImage();
            imgPath = getImgFullPath(imgName, itemCategory);
            try {
                img = getImg(imgPath);
            } catch (IOException e1) {
                img = getImgIfItemHasNoSpecificImg(itemCategory);
            }
        } else {
            img = getImgIfItemHasNoSpecificImg(itemCategory);
        }
        return createBase64ImgData(img);
    }

    private static BufferedImage getImgIfItemHasNoSpecificImg(final String itemCategory) {
        String imgName = getCategoryDefaultImgName(itemCategory);
        String imgPath = getImgFullPath(imgName, itemCategory);

        BufferedImage img = null;
        try {
            img = getImg(imgPath);
        } catch (IOException e1) {
            e1.printStackTrace();
            try {
                img = getImg(imgPath);
            } catch (IOException e2) {
                e2.printStackTrace();
                imgName = getItemDefaultImgName();
                imgPath = IMG_DIRECTORY_PATH + "common/" + imgName;
                try {
                    img = getImg(imgPath);
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return img;
    }

    public static BufferedImage getImg(String imgPath) throws IOException {
        final File file = new File(imgPath);
        return ImageIO.read(file);
    }

    public static void createImgFileInFileSystem(final String base64Data, final Item item) throws IOException {
        final String itemCategory = item.getCategory();
        final String imgName = getImgName(itemCategory, item.getName());

        createCategoryDirectoryIfNotExists(itemCategory);

        byte[] decodedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        final ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        BufferedImage img = ImageIO.read(bis);
        final String imgPath = getImgFullPath(imgName, itemCategory);
        final File file = new File(imgPath);
        ImageIO.write(img, PNG_EXTENSION, file);
    }

    public static String createBase64ImgData(final BufferedImage img) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, PNG_EXTENSION, baos);
        } catch (IOException e) {
            return "-";
        }
        return IMG_DATA_METADATA + DatatypeConverter.printBase64Binary(baos.toByteArray());
    }

    public static String getAppImgData() {
        final String imgPath = IMG_DIRECTORY_PATH + "common/ic_launcher.png";
        try {
            final BufferedImage img = getImg(imgPath);
            return createBase64ImgData(img);
        } catch (IOException e) {
            return "-";
        }
    }

    public static String getImgName(final String itemCategory, final String itemName) {
        return toPath(itemCategory) + "_" + toPath(itemName) + "." + PNG_EXTENSION;
    }

    public static boolean isPngFile(final String base64Data) {
        return getBase64DataFileExtension(base64Data).equals(PNG_EXTENSION);
    }

    private static void createCategoryDirectoryIfNotExists(final String itemCategory) {
        final File directory = new File(getCategoryDirectoryPath(itemCategory));
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static String getImgFullPath(final String imgName, final String itemCategory) {
        return getCategoryDirectoryPath(itemCategory) + "/" + imgName;
    }

    private static String getCategoryDirectoryPath(final String itemCategory) {
        return IMG_DIRECTORY_PATH + toPath(itemCategory);
    }

    private static String getCategoryDefaultImgName(final String itemCategory) {
        return toPath(itemCategory) + "_default.png";
    }

    private static String toPath(final String name) {
        return name.replaceAll(" ", "_").toLowerCase();
    }

    private static String getItemDefaultImgName() {
        return "default_image_small.png";
    }

    private static String getBase64DataFileExtension(final String base64Data) {
        return base64Data.split(";")[0].split("/")[1];
    }

}
