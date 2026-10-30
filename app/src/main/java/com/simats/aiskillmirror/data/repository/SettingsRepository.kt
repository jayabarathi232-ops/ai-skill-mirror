package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserSettingsData
import com.simats.aiskillmirror.data.network.UserSession

class SettingsRepository {
    suspend fun getSettings(userId: Int = UserSession.userId): NetworkResult<UserSettingsData> {
        return ApiClient.fetchSettings(userId)
    }

    suspend fun updateSettings(
        userId: Int = UserSession.userId,
        reminders: Boolean? = null,
        ai: Boolean? = null,
        voice: Boolean? = null,
        appearance: String? = null,
        language: String? = null
    ): NetworkResult<Boolean> {
        return ApiClient.updateSettings(userId, reminders, ai, voice, appearance, language)
    }
}

