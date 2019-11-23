package com.pazukdev.backend;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFinder {

    public Path getDictionaryFilePath() {
        final URL url = getClass().getResource("/dictionary.txt");
        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}
