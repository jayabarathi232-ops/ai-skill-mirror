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
        val title = intent.getStringExtra("reminder_title") ?: "It's time for your daily practice!"
        val message = intent.getStringExtra("reminder_message") ?: "Your Public Speaking practice is waiting. Keep your streak alive!"

        // 1. Show the local Android push notification
        SmartNotificationManager.showLocalNotification(
            context = context,
            title = title,
            message = message
        )

        // 2. Persist notification into backend database asynchronously
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.createNotification(
                    userId = 1,
                    title = title,
                    description = message,
                    category = "learning",
                    iconType = "bell"
                )
            } catch (_: Exception) {
                // Silently handle if offline
            } finally {
                pendingResult.finish()
            }
        }
    }
}

