package com.example.smartscan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化工具类。
 *
 * 数据库中保存 long 时间戳，页面展示时再格式化成可读日期。
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
        // 用于首页“今日新增”统计：查询 createTime >= 今天零点 的资料数量。
        // 这里按中国时区粗略换算，满足课程演示场景。
        long now = System.currentTimeMillis();
        return now - (now + 8 * 60 * 60 * 1000L) % (24 * 60 * 60 * 1000L);
    }
}
