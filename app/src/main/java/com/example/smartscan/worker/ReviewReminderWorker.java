package com.example.smartscan.worker;

import android.content.Context;

/**
 * 复习提醒 Worker 占位类。后续接入 WorkManager 后，可在这里执行每日提醒通知逻辑。
 *
 * 答辩重点：
 * 课程要求体现后台任务/提醒扩展能力，当前阶段先保留类和方法入口。
 * 真实通知逻辑放到后续版本实现，避免当前 OCR 和 CRUD 主线过度复杂。
 */
public class ReviewReminderWorker {
    private final Context context;

    public ReviewReminderWorker(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDailyReviewReminder() {
        // 第一阶段只保留接口，后续可替换为 WorkManager 的 Worker 实现。
        // 例如：查询未掌握错题数量，构造通知，提醒用户复习。
    }
}
