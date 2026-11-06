package com.simats.aiskillmirror.data.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.MainActivity
import com.simats.aiskillmirror.R
import java.util.Calendar

object SmartNotificationManager {

    const val CHANNEL_ID = "skill_mirror_smart_reminders"
    const val CHANNEL_NAME = "Daily Practice Reminders & Alerts"
    const val CHANNEL_DESC = "Notifications for scheduled skill practice, session completion, and streak momentum"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
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

    fun showLocalNotification(
        context: Context,
        title: String,
        message: String,
        targetScreen: String = "SPEECH_STUDIO",
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
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 150, 250))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun scheduleDailyPracticeReminder(
        context: Context,
        hourOfDay: Int,
        minute: Int,
        title: String = "Daily Practice Reminder",
        message: String = "It's time for your Public Speaking practice. Keep your streak alive!"
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("reminder_title", title)
            putExtra("reminder_message", message)
            putExtra("target_screen", "SPEECH_STUDIO")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
            // Alarm handled gracefully
        }
    }

    fun cancelPracticeReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showPracticeReminder(context: Context, timeText: String, skillName: String = "Public Speaking") {
        showLocalNotification(
            context = context,
            title = "Daily Practice Reminder",
            message = "It's $timeText! Time for your $skillName practice.",
            targetScreen = "SPEECH_STUDIO"
        )
    }

    fun showFiveMinuteWarning(context: Context) {
        showLocalNotification(
            context = context,
            title = "5 Minutes Remaining!",
            message = "5 minutes left in your session. Finish strong!",
            targetScreen = "SPEECH_STUDIO"
        )
    }

    fun showSessionCompleted(context: Context) {
        showLocalNotification(
            context = context,
            title = "Practice Session Over!",
            message = "Your practice time is over! Great job completing today's session.",
            targetScreen = "SPEECH_STUDIO"
        )
    }
}
