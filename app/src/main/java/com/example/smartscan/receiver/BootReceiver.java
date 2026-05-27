package com.example.smartscan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smartscan.util.AppPreferenceManager;
import com.example.smartscan.worker.ReminderScheduler;

/**
 * 开机广播接收器。后续用于手机重启后重新注册每日复习提醒。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppPreferenceManager manager = new AppPreferenceManager(context);
            new ReminderScheduler(context).scheduleDailyReminder(manager.isReminderEnabled(), manager.getReminderTime());
        }
    }
}
