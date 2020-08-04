package com.pazukdev.backend.util;

import com.opencsv.CSVReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Siarhei Sviarkaltsau
 */
public class FileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static final String GOOGLE_DOCUMENT_BASIC_URL = "https://docs.google.com/";

    public static class GoogleDocumentTypeUrl {
        public static final String DOCUMENT = GOOGLE_DOCUMENT_BASIC_URL + "document/d/";
        public static final String SPREADSHEET = GOOGLE_DOCUMENT_BASIC_URL + "spreadsheets/d/";
    }

    public static class Directory {
        public static String BASIC_DIRECTORY = "backend/src/";
        public static final String STATIC_DIRECTORY = "/static/";
    }

    public static class FileFormat {
        public static final String CSV = "csv";
        public static final String TXT = "txt";
    }

    public static class FileId {
        public static final String COMMENT = "1g8YeaINmlH26XS1rqJ0oJRh0BN8mN8MIVRBh2MG4GQE";
        public static final String INFO_CATEGORY = "1JM_dDZIKjCRvrkOLRvvNwGtP3Al-Rakgtu-w4dFgB-c";
        public static final String LANG = "1XwULMlxG5JM5VYU-3qTmXYguF_kPvKFb2zE2iIFi_o0";
        public static final String USER = "1lQvD9rQYheddn-D1k8JmnUPb7fRJr6TkJVdcwPzdTmo";
    }

    public static List<String> getComments() {
        return readGoogleDocDocument(FileId.COMMENT);
    }

    public static List<String> getInfoCategories() {
        return readGoogleDocDocument(FileId.INFO_CATEGORY);
    }

    public static List<String> readGoogleDocDocument(final String documentId) {
        try (final InputStream in = openGoogleDoc(documentId, GoogleDocumentTypeUrl.DOCUMENT, FileFormat.TXT)) {
            final List<String> fileLines = IOUtils.readLines(Objects.requireNonNull(in), StandardCharsets.UTF_8);
            final String firstLine = SpecificStringUtil.removeUtf8BOM(fileLines.get(0));
            fileLines.remove(0);
            fileLines.add(0, firstLine);
            CollectionUtil.removeAllEmpty(fileLines);
            return fileLines;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<List<String>> readGoogleDocSpreadsheet(final String documentId) {
        List<String[]> lines = null;
        try (final InputStream in = openGoogleDoc(documentId, GoogleDocumentTypeUrl.SPREADSHEET, FileFormat.CSV)) {
            lines = new CSVReader(new InputStreamReader(Objects.requireNonNull(in))).readAll();
        } catch (IOException e) {
            LOGGER.error("Error collecting data from input stream", e);
        }
        return CollectionUtil.listOfArraysToListOfLists(Objects.requireNonNull(lines));
    }

//    public static void main(String[] args) throws IOException {
//        final URL url = new URL("https://www.autodoc.by/price/1307/62032RS");
//        final InputStream in = Objects.requireNonNull(url).openStream();
//        final List<String> fileLines = IOUtils.readLines(Objects.requireNonNull(in), StandardCharsets.UTF_8);
//        fileLines.forEach(System.out::println);
//    }

    private static InputStream openGoogleDoc(final String documentId,
                                             final String googleDocumentTypeUrl,
                                             final String exportFormat) {        try {
            final URL url = new URL(googleDocumentTypeUrl + documentId + "/export?format=" + exportFormat);
            return Objects.requireNonNull(url).openStream();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getItemsDataGoogleSheetsIds(final boolean fromGoogleDocs) {
        if (true) {
            final String fileId = "147-PuU3hmvjcvb4uIJHQ3zy3UMLFRvul4oOsbdXRO5M";
            final List<String> dataFilesIds = new ArrayList<>();
            for (final String line : readGoogleDocDocument(fileId)) {
                dataFilesIds.add(line.split("//")[0].trim());
            }
            return dataFilesIds.toArray(new String[0]);
        }

        return new String[] {
                "1N6Y6uavkv6AVzIF5-NbRlXOZTJekHFBrtF8o4VvtDxc", // manufacturer
                "12rB_t_ImCGscKiGPazc4WodiG0b9G4zOOfTW1aIbbak", // standard
                "1R2haf5EqWqC6YJ4GJkcwDyTBB_1ItU_wk3l5zI9CQjQ", // "material"
                "1xMlGApVOsvW3m5eX-0iG98xmKrF3i9-Z6WIlPTPuYr8", // coil
                "1VjpHenQrkv9bxRU377m5c93UCKP1Hk5ZOpZhSkNeOBg", // brush
//                "1Kc6IIZNozbgpjwAzYbiLSnPqihOTKxzZ3lwQXO0QZa0", // "cylinder"
                "1SJcfsoOmpMA5auCEWprmfu5_5hyL_LQTPrT95fuKyvk", // "wire"
                "1qtV8j8nHb8u3gyJtrByI6diDPlVHsYl7X0j4HqgOSik", // "gasket"
                "1LzrET_v01fg-BdtMV8No7nnhBDZxZSAtUMfLwvX2bCA", // "washer"
                "1upjGjUKjtr1tk3RxQ5kR8TKAPbJhhZ-yNwuKwm5Sow4", // "fastener"
                "1Epb-XZlBRUG3lVya_Ps8tz7sLQFxLcO_ezM_Yxsg4tA", // "oil"
                "1SoU8eSo74SGvdbSnx95eOdGST-_Yq8FlzDcXqcr7AB4", // "tube"
                "1hDzM8xIDnqW8GwV1smFTwwzVzlUz_QZcYFx_AFBjSqg", // "tire"
                "1JJrNlsIyT1fCpEu9SKDpYF_CwYgljCqtET3k9i2sGIY", // "seal"
                "1cGNnAl-nkH-pRsnGpeUhfbV7mBe6mTHQq0fg-dhZGvk", // "spark_plug"
                "1OQydFcMrVbis1acHjgoUZ6Q7dbSbj7e2GnImrWL7Bf0", // "lock_ring"
                "1t2GRH9l6PeE31NV44Po2jheeO6pokGyv-1wzrOyL0bs", // "adapter"
                "1-wjosk677Z3iBk-gXA7WgryffXdpsD0pcPk8ld6U3Uo", // "oil_filter"
                "1uqnSTXHP8PFUrqHNdwU9WoyV2tw8gZRT2yMsmVV6VE8", // "air filter"
                "1Gt2zBapfxHsK9ckkRl05IrVr6gl8ud3VmhAvzg71xI8", // "piston_rings"
                "1RsCg2OtsIyUOsbKUVFuvASjOmLJW2GzHU_mZNN4fpWc", // "piston_pin"
                "1Zm2FYY_jMduOaGNQgkCfvgi_pkga1hFHdf1lvy8mj20", // "piston"
                "1WZQ0cFKzwmkUshVtsHAHYmspOV1xrL3WtHgq18uyRKc", // "piston_assembly"
                "1fTjB2FmhIKGvnbE88Cn4ev-qxee1pmt0PYX4QujnGh4", // "universal_joint_cross"
                "1aVsbVKPvEj4vkGAdPtPefnohfB58OKCLFAchgDEyaPc", // "rolling_element"
                "1MN2EPwmY4ob0P1UD-S6tQ1ArjNzlBpVXCsrIOAeq1O4", // "cage"
                "1q8gBtUsPx1nept2SrOqwKdd4soEtYTXiDGaWp78iiT8", // "bearing"
                "1JVkzi1VtZ0UnNDXA8t9Ie3KSfXcUqijpD2n-CUuXI_c", // "rubber_part"
                "1MgrDl6OSBNxp0NgwxqV5atq9UC6D5b4F-2u-Us3Ha00", // "universal_joint"
                "1_DwfWapbBDnyycxxjqEMJKbFv3TK7-9G3jqgRkUcRSM", // "wheel"
                "1TGB_CUz-bsAwYdijOKDh9PbGHP0TXVIkpMavXWJfYRE", // "chassis"
                "1Sg1LQgSpcwUNUUV_PMAcZVHgPxIi3hRoULYP_waET_c", // "generator"
                "1whfpZjfZk2TlmqdUwV60sOJmVoZMc8sBcDq8Lwldcx0", // "sidecar_reduction_drive"
                "12pIYehuWhEmwhhzlcRKxRm2rSFsDytJyu6RDOMQl4xY", // "final_drive"
                "1GAHw2DFmyYdPbwQNtpxwSKrI1mTCe7XTLmPhDIvZhc4", // "gearbox"
                "1VlxspbLk6Vm4aOQnNaJJE6Z_b7bgs5tKABS8CtXHfZU", // "engine"
                "1TmLYCnMjXN5HOqRQ5QD32nQrL4XsTbdeYpS3CfLF3lo" // "vehicle"
        };
    }

}
