package com.pazukdev.backend.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtil {

    private static String BASIC_DIRECTORY = "backend/src/";
    public static final String STATIC_DIRECTORY = "/static/";
    public static final String CSV = "csv";

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

    public static String[] getCSVFilesPaths() {
        final String[] fileNames = {
                "manufacturer",
                "standard",
                "material",
                "wire",
                "gasket",
                "washer",
                "fastener",
                "oil",
                "tube",
                "tire",
                "seal",
                "spark_plug",
                "lock_ring",
                "adapter",
                "oil_filter",
                "piston_rings",
                "piston_pin",
                "piston",
                "piston_assembly",
                "universal_joint_cross",
                "rolling_element",
                "cage",
                "bearing",
                "universal_joint",
                "wheel",
                "chassis",
                "generator",
                "sidecar_reduction_drive",
                "final_drive",
                "gearbox",
                "engine",
                "vehicle"
        };
        final List<String> paths = new ArrayList<>();
        for (final String fileName : fileNames) {
            paths.add(dataFilePathInResources(fileName));
        }
        return paths.toArray(new String[0]);
    }

    private static String dataFilePathInResources(final String fileName) {
        return STATIC_DIRECTORY + fileName + "." + CSV;
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
