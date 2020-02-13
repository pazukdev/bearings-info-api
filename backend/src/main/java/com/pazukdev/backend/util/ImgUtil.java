package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.ImgViewData;
import com.pazukdev.backend.dto.view.AbstractView;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.dto.view.UserView;
import com.pazukdev.backend.entity.AbstractEntity;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.UserEntity;

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

    public static ImgViewData getImg(final UserEntity user) {
        final String imgPath = getImgPath(user.getImg(), "user", false);

        final ImgViewData imgViewData = new ImgViewData();
        imgViewData.setImg(imgPath);
        imgViewData.setDefaultImg("user/default.png");
        return imgViewData;
    }

    public static ImgViewData getImg(final Item item) {
        final String itemImg = item.getImg();
        final String itemCategory = item.getCategory();
        final String imgPath = getImgPath(itemImg, itemCategory, false);
        final String defaultImgPath = getImgIfItemHasNoSpecificImg(itemCategory);

        final ImgViewData imgViewData = new ImgViewData();
        imgViewData.setImg(imgPath);
        imgViewData.setDefaultImg(defaultImgPath);

        try {
            getImg(IMG_DIRECTORY_PATH + defaultImgPath);
        } catch (IOException e) {
            imgViewData.setDefaultImg(getItemDefaultImgPath());
        }

        return imgViewData;
    }

    private static String getImgIfItemHasNoSpecificImg(final String itemCategory) {
        String imgName = getCategoryDefaultImgName(itemCategory);
        return getImgPath(imgName, itemCategory, false);
    }

    public static BufferedImage getImg(String imgPath) throws IOException {
        final File file = new File(imgPath);
        return ImageIO.read(file);
    }

    public static void createImgFileInFileSystem(final String base64Data,
                                                 final AbstractEntity entity) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        final ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        BufferedImage img = ImageIO.read(bis);

        String itemCategory = "";
        String imgName = "";
        if (entity instanceof Item) {
            final Item item = (Item) entity;
            itemCategory = item.getCategory();
            imgName = getItemImgName(itemCategory, item.getName());
            createCategoryDirectoryIfNotExists(itemCategory);
        } else if (entity instanceof UserEntity) {
            final UserEntity user = (UserEntity) entity;
            itemCategory = "user";
            imgName = getUserImgName(user.getName());
        }

        final String imgPath = getImgPath(imgName, itemCategory, true);
        if (imgPath == null) {
            return;
        }

        final File file = new File(imgPath);
        ImageIO.write(img, PNG_EXTENSION, file);
    }

    public static void updateImg(final ItemView view, final Item item) {
        final String imgName = getNewImg(view, item);
        if (imgName != null) {
            item.setImg(imgName);
        }
    }

    public static void updateImg(final UserView view, final UserEntity user) {
        final String imgName = getNewImg(view, user);
        if (imgName != null) {
            user.setImg(imgName);
        }
    }

    private static boolean isBase64Data(final String data) {
        return data != null && data.contains(";base64,");
    }

    private static String getNewImg(final AbstractView abstractView, final AbstractEntity entity) {
        final String imgData = abstractView.getImg();
        if (imgData == null) {
            return "-";
        }
//        if (LinkUtil.isUrl(imgData) && (imgData.contains("https:") || imgData.contains("http:"))) {
//            return imgData;
//        }
        if (LinkUtil.isUrl(imgData)) {
            return imgData;
        }
        if (!isBase64Data(abstractView.getImg()) || !ImgUtil.isPngFile(imgData)) {
            return null;
        }

        try {
            ImgUtil.createImgFileInFileSystem(imgData, entity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgName = null;
        if (entity instanceof Item) {
            final Item item = (Item) entity;
            imgName = ImgUtil.getItemImgName(item.getCategory(), item.getName());
        } else if (entity instanceof UserEntity) {
            final UserEntity user = (UserEntity) entity;
            imgName = ImgUtil.getUserImgName(user.getName());
        }
        return imgName;
    }

    public static String createBase64ImgData(final BufferedImage img) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, PNG_EXTENSION, baos);
        } catch (final Exception e) {
            return "-";
        }
        return IMG_DATA_METADATA + DatatypeConverter.printBase64Binary(baos.toByteArray());
    }

    public static String getAppImgData() {
        return "common/ic_launcher.png";
    }

    public static String getItemImgName(final String itemCategory, final String itemName) {
        return toPath(itemCategory) + "_" + toPath(itemName) + "." + PNG_EXTENSION;
    }

    public static String getUserImgName(final String userName) {
        return toPath(userName) + "." + PNG_EXTENSION;
    }

    public static boolean isPngFile(final String base64Data) {
        return getBase64DataFileExtension(base64Data).equals(PNG_EXTENSION);
    }

    private static void createCategoryDirectoryIfNotExists(final String itemCategory) {
        final File directory = new File(getDirectoryPath(itemCategory, true));
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static String getImgPath(final String itemImg, final String itemCategory, final boolean save) {
        if (itemImg == null || itemCategory == null) {
            return null;
        }
        if (LinkUtil.isUrl(itemImg)) {
            return itemImg;
        }
        return getDirectoryPath(itemCategory, save) + "/" + itemImg;
    }

    private static String getDirectoryPath(final String itemCategory, final boolean save) {
        final String firstPart = save ? IMG_DIRECTORY_PATH : "";
        final String secondPart = itemCategory.equals("user") ? "user/" : toPath(itemCategory);
        return firstPart + secondPart;
    }

    private static String getCategoryDefaultImgName(final String itemCategory) {
        return toPath(itemCategory) + "_default.png";
    }

    private static String toPath(final String name) {
        return name.replaceAll(" ", "_").toLowerCase();
    }

    private static String getItemDefaultImgPath() {
        return "common/default_image_small.png";
    }

    private static String getBase64DataFileExtension(final String base64Data) {
        return base64Data.split(";")[0].split("/")[1];
    }

}
