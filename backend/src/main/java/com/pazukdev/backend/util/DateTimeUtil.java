package com.pazukdev.backend.util;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static String now() {
        final LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(ofPattern("dd.MM.yyyy")) + " " + dateTime.format(ofPattern("HH:mm"));
    }

}
