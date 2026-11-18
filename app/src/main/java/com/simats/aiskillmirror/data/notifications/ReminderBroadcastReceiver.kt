package com.simats.aiskillmirror.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simats.aiskillmirror.data.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ReminderBroadcastReceiver
 *
 * Receives:
 * 1. AlarmManager exact reminder alarms -> displays notification, records into DB, chains next day.
 * 2. System boot / package replace / timezone change broadcasts -> restores scheduled alarms.
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        // 1. Handle Device Reboot, Timezone Changes, or App Updates
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == "android.intent.action.TIME_SET" ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            SmartNotificationManager.rescheduleSavedReminders(context)
            return
        }

        // 2. Handle Practice Reminder Alarm Trigger
        val title = intent.getStringExtra("reminder_title") ?: "Daily Practice Reminder"
        val message = intent.getStringExtra("reminder_message") ?: "Your AI Skill Mirror practice session is waiting!"
        val targetScreen = intent.getStringExtra("target_screen") ?: "DAILY_TRAINING"
        val skillName = intent.getStringExtra("skill_name") ?: "Public Speaking"
        val userId = intent.getIntExtra("user_id", 1)
        val hour = intent.getIntExtra("reminder_hour", 18)
        val minute = intent.getIntExtra("reminder_minute", 0)
        val isTest = intent.getBooleanExtra("is_test", false)

        // Show local Android notification with High Priority
        SmartNotificationManager.showLocalNotification(
            context = context,
            title = title,
            message = message,
            targetScreen = targetScreen
        )

        // If this was a regular daily reminder (not a one-off test), chain and reschedule next day's exact alarm
        if (!isTest) {
            SmartNotificationManager.scheduleDailyPracticeReminder(
                context = context,
                hourOfDay = hour,
                minute = minute,
                skillName = skillName,
                userId = userId,
                targetScreen = targetScreen
            )
        }

        // Asynchronously persist notification into backend database
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.createNotification(
                    userId = userId,
                    title = title,
                    description = message,
                    badgeText = "REMINDER",
                    iconType = "ALARM",
                    category = "PRACTICE"
                )
            } catch (_: Exception) {
                // Silently handle if offline
            } finally {
                pendingResult.finish()
            }
        }
    }
}
