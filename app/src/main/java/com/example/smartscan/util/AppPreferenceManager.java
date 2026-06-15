package com.example.smartscan.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类，保存默认科目、提醒开关和提醒时间。
 *
 * 答辩重点：
 * SharedPreferences 适合保存少量配置项；SQLite 更适合保存结构化资料数据。
 */
public class AppPreferenceManager {
    private static final String PREF_NAME = "smart_scan_settings";
    private static final String KEY_DEFAULT_SUBJECT = "default_subject";
    private static final String KEY_REMINDER_ENABLED = "reminder_enabled";
    private static final String KEY_REMINDER_TIME = "reminder_time";

    private final SharedPreferences preferences;

    public AppPreferenceManager(Context context) {
        // 使用 applicationContext，避免 Fragment/Activity 销毁后持有页面引用。
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getDefaultSubject() {
        return preferences.getString(KEY_DEFAULT_SUBJECT, "高数");
    }

    public void setDefaultSubject(String subject) {
        preferences.edit().putString(KEY_DEFAULT_SUBJECT, subject).apply();
    }

    public boolean isReminderEnabled() {
        return preferences.getBoolean(KEY_REMINDER_ENABLED, false);
    }

    public void setReminderEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply();
    }

    public String getReminderTime() {
        return preferences.getString(KEY_REMINDER_TIME, "20:30");
    }

    public void setReminderTime(String time) {
        preferences.edit().putString(KEY_REMINDER_TIME, time).apply();
    }
}
