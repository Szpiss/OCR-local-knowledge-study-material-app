package com.example.smartscan.worker;

import android.content.Context;

/**
 * 提醒任务调度类。第一阶段只做接口预留，后续可统一注册 WorkManager 或 AlarmManager。
 */
public class ReminderScheduler {
    private final Context context;

    public ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDailyReminder(boolean enabled, String reminderTime) {
        // 后续扩展：根据 enabled 和 reminderTime 注册每日复习提醒任务。
    }

    public void cancelReminder() {
        // 后续扩展：取消已经注册的提醒任务。
    }
}
