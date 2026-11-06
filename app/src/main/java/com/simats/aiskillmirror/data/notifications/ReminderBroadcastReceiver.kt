package com.simats.aiskillmirror.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simats.aiskillmirror.data.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("reminder_title") ?: "Daily Practice Reminder"
        val message = intent.getStringExtra("reminder_message") ?: "Time for your Public Speaking practice. Keep your streak alive!"
        val targetScreen = intent.getStringExtra("target_screen") ?: "SPEECH_STUDIO"

        // 1. Show the local Android push notification
        SmartNotificationManager.showLocalNotification(
            context = context,
            title = title,
            message = message,
            targetScreen = targetScreen
        )

        // 2. Persist notification into backend database asynchronously
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.createNotification(
                    userId = 1,
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
