package com.percy.messageboard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtils {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TimeUtils() {
    }

    public static String format(LocalDateTime time) {
        return time == null ? "" : DISPLAY.format(time);
    }

    public static String iso(LocalDateTime time) {
        return time == null ? "" : ISO.format(time);
    }
}
