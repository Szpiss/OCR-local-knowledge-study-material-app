package com.example.smartscan.worker;

import android.content.Context;

/**
 * 复习提醒 Worker 占位类。后续接入 WorkManager 后，可在这里执行每日提醒通知逻辑。
 */
public class ReviewReminderWorker {
    private final Context context;

    public ReviewReminderWorker(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDailyReviewReminder() {
        // 第一阶段只保留接口，后续可替换为 WorkManager 的 Worker 实现。
    }
}
