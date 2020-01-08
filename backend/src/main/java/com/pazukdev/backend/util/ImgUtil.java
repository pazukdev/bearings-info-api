package com.pazukdev.backend.util;

import com.pazukdev.backend.constant.security.Role;
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

    public static String getUserImgData(final UserEntity user) {
        final Role userRole = user.getRole();
        if (userRole == Role.USER || userRole == Role.GUEST) {
            return "-";
        }

        BufferedImage img = null;
        String imgName = user.getImg();
        String imgPath = getUserIconDirectoryPath() + imgName;
        try {
            img = getImg(imgPath);
        } catch (IOException e1) {
            try {
                imgName = "default.png";
                imgPath = getUserIconDirectoryPath() + imgName;
                img = getImg(imgPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return createBase64ImgData(img);
    }

    public static ImgViewData getImgViewData(final Item item) {
        final String itemCategory = item.getCategory();
        boolean defaultImg = false;
        String imgName;
        String imgPath;
        BufferedImage img;
        if (item.getImage() != null) {
            imgName = item.getImage();
            imgPath = getImgFullPath(imgName, itemCategory);
            try {
                img = getImg(imgPath);
                defaultImg = img != null;
            } catch (IOException e1) {
                img = getImgIfItemHasNoSpecificImg(itemCategory);
            }
        } else {
            img = getImgIfItemHasNoSpecificImg(itemCategory);
        }
        return new ImgViewData(defaultImg, createBase64ImgData(img));
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

    public static void createImgFileInFileSystem(final String base64Data,
                                                 final AbstractEntity entity) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        final ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        BufferedImage img = ImageIO.read(bis);
        String imgPath = null;

        if (entity instanceof Item) {
            final Item item = (Item) entity;
            final String itemCategory = item.getCategory();
            final String imgName = getItemImgName(itemCategory, item.getName());
            createCategoryDirectoryIfNotExists(itemCategory);
            imgPath = getImgFullPath(imgName, itemCategory);
        } else if (entity instanceof UserEntity) {
            final UserEntity user = (UserEntity) entity;
            final String imgName = getUserImgName(user.getName());
            imgPath = getUserIconDirectoryPath() + imgName;
        }

        if (imgPath == null) {
            return;
        }

        final File file = new File(imgPath);
        ImageIO.write(img, PNG_EXTENSION, file);
    }

    public static void updateImg(final ItemView view, final Item item) {
        final String imgName = getNewImg(view, item);
        if (imgName != null) {
            item.setImage(imgName);
        }
    }

    public static void updateImg(final UserView view, final UserEntity user) {
        final String imgName = getNewImg(view, user);
        if (imgName != null) {
            user.setImg(imgName);
        }
    }

    private static String getNewImg(final AbstractView abstractView, final AbstractEntity entity) {
        if (abstractView.getMessages().contains("img removed")) {
            return "-";
        } else if (!abstractView.getMessages().contains("img uploaded")) {
            return null;
        }

        final String base64Data = abstractView.getImgData();
        if (!ImgUtil.isPngFile(base64Data)) {
            return null;
        }

        try {
            ImgUtil.createImgFileInFileSystem(base64Data, entity);
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
        final String imgPath = IMG_DIRECTORY_PATH + "common/ic_launcher.png";
        try {
            final BufferedImage img = getImg(imgPath);
            return createBase64ImgData(img);
        } catch (IOException e) {
            return "-";
        }
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

    private static String getUserIconDirectoryPath() {
        return IMG_DIRECTORY_PATH + "user/";
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
