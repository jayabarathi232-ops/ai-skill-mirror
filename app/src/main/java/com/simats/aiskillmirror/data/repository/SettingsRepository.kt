package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserSettingsData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.RetrofitClient
import com.simats.aiskillmirror.network.SettingsUpdateRequest

class SettingsRepository {
    suspend fun getSettings(userId: Int = UserSession.userId): NetworkResult<UserSettingsData> {
        return try {
            val response = RetrofitClient.apiService.getSettings(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val d = response.body()!!.data!!
                val data = UserSettingsData(
                    isLearningRemindersEnabled = d.isLearningRemindersEnabled,
                    isPersonalizedAiEnabled = d.isPersonalizedAiEnabled,
                    isSaveVoiceRecordingsEnabled = d.isSaveVoiceRecordingsEnabled,
                    currentAppearanceText = d.currentAppearanceText,
                    currentLanguageText = d.currentLanguageText,
                    appVersionText = d.appVersionText
                )
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchSettings(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchSettings(userId)
        }
    }

    suspend fun updateSettings(
        userId: Int = UserSession.userId,
        reminders: Boolean? = null,
        ai: Boolean? = null,
        voice: Boolean? = null,
        appearance: String? = null,
        language: String? = null
    ): NetworkResult<Boolean> {
        return try {
            val req = SettingsUpdateRequest(
                userId = userId,
                learningReminders = reminders,
                personalizedAi = ai,
                saveVoiceRecordings = voice,
                appearance = appearance,
                language = language
            )
            val response = RetrofitClient.apiService.updateSettings(req)
            if (response.isSuccessful && response.body()?.status == "success") {
                NetworkResult(true, response.body()?.message ?: "Settings updated", true)
            } else {
                ApiClient.updateSettings(userId, reminders, ai, voice, appearance, language)
            }
        } catch (e: Exception) {
            ApiClient.updateSettings(userId, reminders, ai, voice, appearance, language)
        }
    }
}
