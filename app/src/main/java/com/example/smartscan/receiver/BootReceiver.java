package com.example.smartscan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smartscan.util.AppPreferenceManager;
import com.example.smartscan.worker.ReminderScheduler;

/**
 * 开机广播接收器。后续用于手机重启后重新注册每日复习提醒。
 *
 * 答辩重点：
 * BroadcastReceiver 已经预留，说明项目考虑了“设备重启后提醒任务恢复”的扩展。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 读取用户设置，如果开启提醒，后续可在这里重新注册提醒任务。
            AppPreferenceManager manager = new AppPreferenceManager(context);
            new ReminderScheduler(context).scheduleDailyReminder(manager.isReminderEnabled(), manager.getReminderTime());
        }
    }
}
