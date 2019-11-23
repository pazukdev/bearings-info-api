package com.pazukdev.backend.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {

    private static String BASIC_DIRECTORY = "backend/src/";

    public static Set<String> getTxtFileLines(final Path path) {
        try {
            return new HashSet<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public static Path getDictionaryFilePath() {
//        return new PathFinder().getDictionaryFilePath();
        return Paths.get(getDictionaryFilePathString());
    }

    public static void createDictionaryFileInFileSystem(final String base64Data) throws IOException {
        createDictionaryFileInFileSystem(Base64.getDecoder().decode(base64Data.getBytes(StandardCharsets.UTF_8)));
    }

    public static void createDictionaryFileInFileSystem(final byte[] text) throws IOException {
        Files.write(getDictionaryFilePath(), text);
    }

    public static String getDictionaryFilePathString() {
        return BASIC_DIRECTORY + "language/dictionary.txt";
    }

}
