package com.example.smartscan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化工具类。
 */
public class DateUtils {
    private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private static final SimpleDateFormat DATE = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);

    public static String formatDateTime(long time) {
        if (time <= 0) {
            return "-";
        }
        return DATE_TIME.format(new Date(time));
    }

    public static String formatShort(long time) {
        if (time <= 0) {
            return "-";
        }
        return DATE.format(new Date(time));
    }

    public static long startOfToday() {
        long now = System.currentTimeMillis();
        return now - (now + 8 * 60 * 60 * 1000L) % (24 * 60 * 60 * 1000L);
    }
}
