package com.pazukdev.backend.tablemodel;

import com.pazukdev.backend.util.CSVFileUtil;
import com.pazukdev.backend.util.SpecificStringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableModelFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(TableModelFactory.class);

    public static TableModelFactory create() {
        return new TableModelFactory();
    }

    public TableModel createTableModel(final String... filePaths) {
        final List<TableRow> tableRows = new ArrayList<>();
        for (final String filePath : filePaths) {
            try (final InputStream inputStream = getClass().getResourceAsStream(filePath)) {
                tableRows.addAll(getTableRows(CSVFileUtil.readInputStreamFromCSVFile(inputStream)));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return new TableModelImpl(tableRows);
    }

    private boolean containsCategory(final List<String> line) {
        return line.contains("Category:") || line.contains("Category: ");
    }

    private boolean isItemSourceFile(final List<List<String>> fileLines) {
        for (List<String> line : fileLines) {
            if (containsCategory(line)) {
                return true;
            }
        }
        return false;
    }

    private List<TableRow> getTableRows(final List<List<String>> fileLines) {
        if (isItemSourceFile(fileLines)) {
            final List<TableRow> rows = new ArrayList<>();
            for (final List<List<String>> list : categorize(fileLines)) {
                rows.addAll(getRows(list));
            }
            return rows;
        } else {
            for (List<String> lines : fileLines) {
                for (String s : lines) {
                    SpecificStringUtil.removeSpaces(s);
                }
            }
            return getRows(fileLines);
        }

    }

    public List<List<List<String>>> categorize(List<List<String>> fileLines) {
        fileLines = removeEmptyLines(removeEmptyElements(fileLines));
        final List<List<List<String>>> listOfCategorizedFileLines = new ArrayList<>();
        List<List<String>> fileLinesSubList = null;
        String category = null;
        boolean header = false;
        for (List<String> line : fileLines) {
            if (line.isEmpty()) {
                continue;
            }
            if (containsCategory(line)) {
                addToListOfCategorizedFileLines(listOfCategorizedFileLines, fileLinesSubList);
                fileLinesSubList = new ArrayList<>();
                category = line.get(1);
                header = true;
                continue;
            }
            if (header) {
                line.add("Category");
                header = false;
            } else {
                line.add(category);
            }
            if (fileLinesSubList != null) {
                fileLinesSubList.add(line);
            }
        }
        addToListOfCategorizedFileLines(listOfCategorizedFileLines, fileLinesSubList);
        return listOfCategorizedFileLines;
    }

    private void addToListOfCategorizedFileLines(final List<List<List<String>>> listOfCategorizedFileLines,
                                                 final List<List<String>> listToAdd) {
        if (listToAdd == null || listToAdd.size() == 0) {
            return;
        }
        listOfCategorizedFileLines.add(listToAdd);
    }

    public List<List<String>> removeEmptyLines(final List<List<String>> fileLines) {
        final List<List<String>> filteredLines = new ArrayList<>();
        for (List<String> line : fileLines) {
            if (line.contains("") && Collections.frequency(line, line.get(0)) == line.size()) {
                continue;
            }
            filteredLines.add(line);
        }
        return filteredLines;
    }

    public List<List<String>> removeEmptyElements(final List<List<String>> fileLines) {
        for (List<String> line : fileLines) {
            line.removeIf("" :: equals);
        }
        return fileLines;
    }

    private List<TableRow> getRows(final List<List<String>> fileLines) {
        final List<TableRow> rows = new ArrayList<>();
        final List<String> header = getHeader(fileLines);
        final List<List<String>> body = getBody(fileLines);

        for (final List<String> line : body) {
            final TableRow row = TableRow.create();
            for (int i = 0; i < line.size(); i++) {
                row.put(header.get(i), line.get(i));
            }
            rows.add(row);
        }

        return rows;
    }

    private List<String> getHeader(final List<List<String>> fileLines) {
        return fileLines.get(0);
    }

    private List<List<String>> getBody(final List<List<String>> fileLines) {
        return fileLines.subList(1, fileLines.size());
    }

}
