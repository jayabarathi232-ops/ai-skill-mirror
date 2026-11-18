package com.simats.aiskillmirror.data.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.MainActivity
import com.simats.aiskillmirror.R
import java.util.Calendar
import java.util.Locale

/**
 * SmartNotificationManager
 *
 * Robust, production-grade Android notification and exact alarm scheduler.
 * Handles:
 * - High priority NotificationChannel creation.
 * - Exact alarms via AlarmManager.setExactAndAllowWhileIdle() with fallback.
 * - Battery optimization / Doze mode bypass for user reminders.
 * - Persistent local SharedPreferences storage of user reminder preferences.
 * - Automatic re-scheduling across device reboots and timezone changes.
 * - Friendly, motivational notification message rotations.
 */
object SmartNotificationManager {

    const val CHANNEL_ID = "skill_mirror_smart_reminders"
    const val CHANNEL_NAME = "Daily Practice Reminders & Alerts"
    const val CHANNEL_DESC = "Notifications for scheduled skill practice, session completion, and streak momentum"

    const val ACTION_DAILY_REMINDER = "com.simats.aiskillmirror.ACTION_DAILY_REMINDER"
    const val REQUEST_CODE_DAILY_REMINDER = 1001
    const val REQUEST_CODE_TEST_REMINDER = 1002

    private const val PREFS_NAME = "skill_mirror_reminders_prefs"
    private const val KEY_ENABLED_PREFIX = "reminder_enabled_user_"
    private const val KEY_HOUR_PREFIX = "reminder_hour_user_"
    private const val KEY_MINUTE_PREFIX = "reminder_minute_user_"
    private const val KEY_TIMETEXT_PREFIX = "reminder_timetext_user_"
    private const val KEY_SKILL_PREFIX = "reminder_skill_user_"
    private const val KEY_DAYS_PREFIX = "reminder_days_user_"
    private const val KEY_NEXT_TRIGGER_PREFIX = "reminder_next_trigger_user_"
    private const val KEY_LAST_ACTIVE_USER_ID = "last_active_reminder_user_id"

    // Friendly motivational reminder messages
    val MOTIVATIONAL_MESSAGES = listOf(
        "Hey! It's time for your daily skill practice!",
        "Your AI Skill Mirror practice session is waiting!",
        "You have pending tasks. Let's complete today's goal!",
        "Great job! Keep improving your skills!"
    )

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                enableLights(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }
    }

    // -------------------------------------------------------------------------
    // Persistent Preferences Management
    // -------------------------------------------------------------------------

    data class ReminderConfig(
        val userId: Int,
        val isEnabled: Boolean,
        val hour: Int,
        val minute: Int,
        val timeText: String,
        val skillName: String,
        val daysText: String,
        val nextTriggerEpoch: Long
    )

    fun saveReminderPreferences(
        context: Context,
        userId: Int,
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        timeText: String,
        skillName: String = "Public Speaking",
        daysText: String = "Mon, Tue, Wed, Thu, Fri, Sat, Sun",
        nextTriggerEpoch: Long = 0L
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("$KEY_ENABLED_PREFIX$userId", isEnabled)
            .putInt("$KEY_HOUR_PREFIX$userId", hour)
            .putInt("$KEY_MINUTE_PREFIX$userId", minute)
            .putString("$KEY_TIMETEXT_PREFIX$userId", timeText)
            .putString("$KEY_SKILL_PREFIX$userId", skillName)
            .putString("$KEY_DAYS_PREFIX$userId", daysText)
            .putLong("$KEY_NEXT_TRIGGER_PREFIX$userId", nextTriggerEpoch)
            .putInt(KEY_LAST_ACTIVE_USER_ID, userId)
            .apply()
    }

    fun getReminderPreferences(context: Context, userId: Int): ReminderConfig {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("$KEY_ENABLED_PREFIX$userId", true)
        val hour = prefs.getInt("$KEY_HOUR_PREFIX$userId", 18)
        val minute = prefs.getInt("$KEY_MINUTE_PREFIX$userId", 0)
        val timeText = prefs.getString("$KEY_TIMETEXT_PREFIX$userId", "06:00 PM") ?: "06:00 PM"
        val skill = prefs.getString("$KEY_SKILL_PREFIX$userId", "Public Speaking") ?: "Public Speaking"
        val days = prefs.getString("$KEY_DAYS_PREFIX$userId", "Mon, Tue, Wed, Thu, Fri, Sat, Sun") ?: "Mon, Tue, Wed, Thu, Fri, Sat, Sun"
        val nextTrigger = prefs.getLong("$KEY_NEXT_TRIGGER_PREFIX$userId", 0L)
        return ReminderConfig(userId, isEnabled, hour, minute, timeText, skill, days, nextTrigger)
    }

    fun getLastActiveUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_LAST_ACTIVE_USER_ID, 1)
    }

    // -------------------------------------------------------------------------
    // Local Push Notification Presentation
    // -------------------------------------------------------------------------

    fun showLocalNotification(
        context: Context,
        title: String,
        message: String,
        targetScreen: String = "DAILY_TRAINING",
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        initNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", targetScreen)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 150, 250))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    // -------------------------------------------------------------------------
    // AlarmManager Exact Scheduling
    // -------------------------------------------------------------------------

    fun scheduleDailyPracticeReminder(
        context: Context,
        hourOfDay: Int,
        minute: Int,
        title: String? = null,
        message: String? = null,
        skillName: String = "Public Speaking",
        userId: Int = 1,
        delayMinutes: Int? = null,
        targetScreen: String = "DAILY_TRAINING"
    ): Long {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return 0L

        // Select a friendly motivational message if not explicitly provided
        val resolvedTitle = title ?: "Daily Practice Reminder"
        val resolvedMessage = message ?: MOTIVATIONAL_MESSAGES.random()

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
            putExtra("reminder_title", resolvedTitle)
            putExtra("reminder_message", resolvedMessage)
            putExtra("skill_name", skillName)
            putExtra("user_id", userId)
            putExtra("reminder_hour", hourOfDay)
            putExtra("reminder_minute", minute)
            putExtra("target_screen", targetScreen)
            putExtra("is_test", delayMinutes != null)
        }

        val requestCode = if (delayMinutes != null) REQUEST_CODE_TEST_REMINDER else REQUEST_CODE_DAILY_REMINDER
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val now = System.currentTimeMillis()
        val triggerEpoch: Long = if (delayMinutes != null && delayMinutes > 0) {
            now + (delayMinutes * 60 * 1000L)
        } else {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            calendar.timeInMillis
        }

        // Save preferences with calculated next trigger epoch
        val amPm = if (hourOfDay >= 12) "PM" else "AM"
        val displayHour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        val timeText = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
        saveReminderPreferences(
            context = context,
            userId = userId,
            isEnabled = true,
            hour = hourOfDay,
            minute = minute,
            timeText = timeText,
            skillName = skillName,
            nextTriggerEpoch = triggerEpoch
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExactAlarms(context)) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpoch,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpoch,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpoch,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpoch,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for extreme permission edge cases
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpoch,
                        pendingIntent
                    )
                }
            } catch (_: Exception) {}
        } catch (_: Exception) {
            // Handled gracefully
        }

        return triggerEpoch
    }

    fun cancelPracticeReminder(context: Context, userId: Int = 1) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        alarmManager.cancel(pendingIntent)

        // Cancel test reminder as well
        val testIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TEST_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        alarmManager.cancel(testIntent)

        // Update preferences
        val current = getReminderPreferences(context, userId)
        saveReminderPreferences(
            context = context,
            userId = userId,
            isEnabled = false,
            hour = current.hour,
            minute = current.minute,
            timeText = current.timeText,
            skillName = current.skillName,
            daysText = current.daysText,
            nextTriggerEpoch = 0L
        )
    }

    /**
     * Reschedules saved active reminders across device reboots or timezone updates.
     */
    fun rescheduleSavedReminders(context: Context) {
        val activeUserId = getLastActiveUserId(context)
        val config = getReminderPreferences(context, activeUserId)
        if (config.isEnabled) {
            scheduleDailyPracticeReminder(
                context = context,
                hourOfDay = config.hour,
                minute = config.minute,
                skillName = config.skillName,
                userId = config.userId
            )
        }
    }

    // -------------------------------------------------------------------------
    // Legacy / Convenience Helpers (Preserved for compatibility)
    // -------------------------------------------------------------------------

    fun showPracticeReminder(context: Context, timeText: String, skillName: String = "Public Speaking") {
        showLocalNotification(
            context = context,
            title = "Daily Practice Reminder",
            message = "It's $timeText! Time for your $skillName practice.",
            targetScreen = "DAILY_TRAINING"
        )
    }

    fun showFiveMinuteWarning(context: Context) {
        showLocalNotification(
            context = context,
            title = "5 Minutes Remaining!",
            message = "5 minutes left in your session. Finish strong!",
            targetScreen = "DAILY_TRAINING"
        )
    }

    fun showSessionCompleted(context: Context) {
        showLocalNotification(
            context = context,
            title = "Practice Session Over!",
            message = "Your practice time is over! Great job completing today's session.",
            targetScreen = "DAILY_TRAINING"
        )
    }
}
