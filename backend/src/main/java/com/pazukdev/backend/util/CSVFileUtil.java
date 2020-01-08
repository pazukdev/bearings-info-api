package com.pazukdev.backend.util;

import com.opencsv.CSVReader;
import com.pazukdev.backend.entity.AbstractEntity;
import com.pazukdev.backend.entity.factory.AbstractEntityFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
public class CSVFileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVFileUtil.class);





    public static List<List<String>> readInputStreamFromCSVFile(final InputStream in) {
        List<String[]> lines = null;
        try (final CSVReader reader = new CSVReader(new InputStreamReader(in))) {
            lines = reader.readAll();
        } catch (IOException e) {
            LOGGER.error("Error collecting data from input stream", e);
        }
        return listOfArraysToListOfLists(lines);
    }

    public static <E extends AbstractEntity> E findByName(final String name,
                                                          final AbstractEntityFactory<E> factory) {
        return factory.findByName(name);
    }

    public static <E extends AbstractEntity> E getFirstEntity(final AbstractEntityFactory<E> factory) {
        return factory.createEntitiesFromCSVFile().get(0);
    }

    private static List<String[]> format(final List<String[]> list) {
        return AppCollectionUtil.toLowerCase(list);
    }



    private static List<List<String>> listOfArraysToListOfLists(final List<String[]> arrays) {
        final List<List<String>> lists = new ArrayList<>();
        for (final String[] array : arrays) {
            lists.add(new ArrayList<>(Arrays.asList(array)));
        }
        return lists;
    }

}



















