package com.example.smartscan.worker;

import android.content.Context;

/**
 * 提醒任务调度类。第一阶段只做接口预留，后续可统一注册 WorkManager 或 AlarmManager。
 *
 * 答辩重点：
 * 当前项目已经保存提醒配置，但真正通知属于后续阶段；这里保留扩展入口。
 */
public class ReminderScheduler {
    private final Context context;

    public ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDailyReminder(boolean enabled, String reminderTime) {
        // 后续扩展：根据 enabled 和 reminderTime 注册每日复习提醒任务。
        // 例如 V1.2 可在这里创建 WorkManager 周期任务或 AlarmManager 定时任务。
    }

    public void cancelReminder() {
        // 后续扩展：取消已经注册的提醒任务。
    }
}
